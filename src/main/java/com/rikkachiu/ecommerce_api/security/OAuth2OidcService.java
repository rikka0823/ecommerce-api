package com.rikkachiu.ecommerce_api.security;

import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.dao.role.RoleDao;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Google Social Login
@Service
public class OAuth2OidcService extends OidcUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Transactional
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 取得 user 資料
        OidcUser oAuth2User = super.loadUser(userRequest);

        // 取得 provider、email、providerUserId
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String providerUserId = (String) attributes.get("sub");

        // 依 email 取得 user、userID
        User user = userDao.getUserByEmail(email);
        int userId = 0;
        if (user != null) {
            userId = user.getUserId();
        }

        // 如果尚未註冊，則建立資料
        if (user == null) {
            // 封裝 userDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setProviderUserId(providerUserId);
            userDTO.setProvider(provider);

            // 建立 role，預設為 ROLE_CUSTOMER
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(Role.ROLE_CUSTOMER);
            userDTO.setRoleSet(roleSet);

            // 資料庫建立會員、角色
            userId = userDao.createUser(userDTO);
            roleDao.createUserHasRole(userId, userDTO.getRoleSet());
        }

        // 封裝權限
        Set<Role> roleSet = roleDao.getRolesById(userId);
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : roleSet) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }

        // 合併不同 OAuth2.0 帳號
        if (user != null && !user.getProvider().equals(provider)) {
            // 封裝 userDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setProviderUserId(providerUserId);
            userDTO.setProvider(provider);

            // 更新會員資料
            userDao.createUser(userDTO);
        }

        return new CustomOidcUser(oAuth2User, authorities);
    }
}
