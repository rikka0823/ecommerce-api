package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.dao.role.RoleDao;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.CodeDTO;
import com.rikkachiu.ecommerce_api.model.dto.RefreshTokenDTO;
import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.KeycloakToken;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${keycloak.client.id}")
    private String KEYCLOAK_CLIENT_ID;

    @Value("${keycloak.client.secret}")
    private String KEYCLOAK_CLIENT_SECRET;

    @Value("${auth.url}")
    private String AUTH_URL;

    @Value("${token.url}")
    private String TOKEN_URL;

    @Value("${response.type}")
    private String RESPONSE_TYPE;

    @Value("${grant.type}")
    private String GRANT_TYPE;

    @Value("${redirect.uri}")
    private String REDIRECT_URI;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleDao roleDao;

    // 依 email 取得用戶資訊
    @Cacheable(cacheNames = "ecommerce_user", key = "'email-' + #p0", unless = "#result == null")
    @Override
    public User getUserByEmail(String email) {
        // 依 email 查詢 user 並封裝 role
        User user = userDao.getUserByEmail(email);
        if (user == null) {
            logger.warn("{}: 查無此 email", email);
            return null;
        }
        user.setRoleSet(roleDao.getRolesById(user.getUserId()));

        return user;
    }

    // 依 email 檢查是否符合當前獲取資源 userId
    @Override
    public void checkUserIdByEmail(Authentication authentication, Jwt jwt, Integer userId) {
        // 取得 user 權限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<Role> roleSet = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            roleSet.add(Role.valueOf(authority.getAuthority()));
        }

        // 依 email 查詢 user
        User user;
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            user = userDao.getUserByEmail(oidcUser.getEmail());
        } else {
            user = userDao.getUserByEmail(jwt.getClaimAsString("email"));
        }

        // 限制 ROLE_CUSTOMER 權限
        if (roleSet.size() == 1 && roleSet.contains(Role.ROLE_CUSTOMER)) {
            if (user.getUserId() != userId) {
                logger.warn("id: {} 未被授權獲取該資源", user.getUserId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }

    // 依 id 取得用戶資訊
    @Cacheable(cacheNames = "ecommerce_user", key = "'userId-' + #p0", unless = "#result == null")
    @Override
    public User getUserById(Integer userId) {
        // 依 id 查詢 user 並封裝 role
        User user = userDao.getUserById(userId);
        if (user == null) {
            return null;
        }
        user.setRoleSet(roleDao.getRolesById(userId));

        return user;
    }

    // 建立帳號
    @CacheEvict(cacheNames = "ecommerce_user", allEntries = true)
    @Transactional
    @Override
    public Integer register(UserDTO userDTO) {
        // 檢查 email 是否已存在
        if (userDao.getUserByEmail(userDTO.getEmail()) != null) {
            logger.warn("email: {} 已存在", userDTO.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 密碼加密
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // 建立 user
        int userId = userDao.createUser(userDTO);

        // 建立 role
        roleDao.createUserHasRole(userId, userDTO.getRoleSet());

        return userId;
    }

    // 登入
    @Override
    public User userLogin(Authentication authentication, Jwt jwt) {
        // 依 email 查詢 user 並封裝 role
        User user;

        // Oauth2.0 來源
        if (jwt == null) {
            if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                user = userDao.getUserByEmail(oidcUser.getEmail());
                user.setRoleSet(roleDao.getRolesById(user.getUserId()));
                return user;
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // JWT 來源
        user = userDao.getUserByEmail(jwt.getClaimAsString("email"));
        user.setRoleSet(roleDao.getRolesById(user.getUserId()));
        return user;
    }

    // 依照 email 更新用戶角色
    @CacheEvict(cacheNames = "ecommerce_user", allEntries = true)
    @Transactional
    @Override
    public User updateUserRolesByEmail(RoleDTO roleDTO) {
        // 依 email 查詢 user 是否存在
        User user = userDao.getUserByEmail(roleDTO.getEmail());
        if (user == null) {
            logger.warn("{}: 查無此 email", roleDTO.getEmail());
            return null;
        }

        // 先刪除後更新
        roleDao.deleteRolesById(roleDTO.getEmail());
        roleDao.createUserHasRole(user.getUserId(), roleDTO.getRoleSet());

        // 封裝 roleSet
        user.setRoleSet(roleDTO.getRoleSet());

        return user;
    }

    // 刪除帳號
    @CacheEvict(cacheNames = "ecommerce_user", allEntries = true)
    @Override
    public void deleteUser(Authentication authentication, Jwt jwt, Integer userId) {
        // 取得 user
        User user;

        // Oauth2.0 來源
        if (jwt == null) {
            // 依 email 取得 user
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            user = getUserByEmail(oidcUser.getEmail());

            // 禁止非 admin 刪除其他人帳號
            if (!user.getRoleSet().contains(Role.ROLE_ADMIN)) {
                if (user.getUserId() != userId) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }
        }

        // JWT 來源
        if (jwt != null) {
            // 依 email 取得 user
            user = getUserByEmail(jwt.getClaimAsString("email"));

            // 禁止非 admin 刪除其他人帳號
            if (!user.getRoleSet().contains(Role.ROLE_ADMIN)) {
                if (user.getUserId() != userId) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }
        }

        userDao.deleteUser(userId);
    }

    // 生成授權網址
    @Override
    public String buildAuthUrl() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(AUTH_URL)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("client_id", KEYCLOAK_CLIENT_ID)
                .queryParam("scope", "profile email openid")
                .queryParam("redirect_uri", REDIRECT_URI);

        return uriComponentsBuilder.toUriString();
    }

    // 獲取 access token 和 refresh token
    @Override
    public KeycloakToken getToken(CodeDTO codeDTO) {
        // 設定 headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 設定 body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", KEYCLOAK_CLIENT_ID);
        body.add("client_secret", KEYCLOAK_CLIENT_SECRET);
        body.add("code", codeDTO.getCode());
        body.add("redirect_uri", REDIRECT_URI);

        // 獲取、更新 token
        KeycloakToken keycloakToken;
        try {
            keycloakToken = new RestTemplate()
                    .postForObject(TOKEN_URL,
                            new HttpEntity<>(body, headers),
                            KeycloakToken.class);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (keycloakToken != null && keycloakToken.getRefreshToken() != null) {
            userDao.updateRefreshTokenByEmail(codeDTO.getEmail(), keycloakToken.getRefreshToken());
        }

        return keycloakToken;
    }

    // 以 refresh_token 換取 access_token
    @Override
    public String exchangeAccessToken(RefreshTokenDTO refreshTokenDTO) {
        // 設定 headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 設定 body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", KEYCLOAK_CLIENT_ID);
        body.add("client_secret", KEYCLOAK_CLIENT_SECRET);
        body.add("refresh_token", refreshTokenDTO.getRefreshToken());

        // 獲取、更新 token
        KeycloakToken keycloakToken;
        try {
            keycloakToken = new RestTemplate()
                    .postForObject(TOKEN_URL,
                            new HttpEntity<>(body, headers),
                            KeycloakToken.class);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return keycloakToken.getAccessToken();
    }
}
