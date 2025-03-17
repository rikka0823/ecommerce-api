package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.dao.role.RoleDao;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleDao roleDao;

    // 依 email 取得用戶資訊
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
    public void checkUserIdByEmail(Authentication authentication, Integer userId) {
        // 取得 user 權限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<Role> roleSet = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            roleSet.add(Role.valueOf(authority.getAuthority()));
        }

        // 依 email 查詢 user
        User user = userDao.getUserByEmail(authentication.getName());

        // 限制 ROLE_CUSTOMER 權限
        if (roleSet.size() == 1 && roleSet.contains(Role.ROLE_CUSTOMER)) {
            if (user.getUserId() != userId) {
                logger.warn("id: {} 未被授權獲取該資源", user.getUserId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }

    // 依 id 取得用戶資訊
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
    public User userLogin(Authentication authentication) {
        // 依 email 查詢 user 並封裝 role
        User user = userDao.getUserByEmail(authentication.getName());
        user.setRoleSet(roleDao.getRolesById(user.getUserId()));

        return user;
    }

    // 依照 email 更新用戶角色
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
    @Override
    public void deleteUser(Authentication authentication, Integer userId) {
        // 依 email 取得 user
        User user = getUserByEmail(authentication.getName());

        // 禁止非 admin 刪除其他人帳號
        if (!user.getRoleSet().contains(Role.ROLE_ADMIN)) {
            if (user.getUserId() != userId) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        userDao.deleteUser(userId);
    }
}
