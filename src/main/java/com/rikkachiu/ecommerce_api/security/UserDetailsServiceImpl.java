package com.rikkachiu.ecommerce_api.security;

import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 依 email 取得用戶資訊
        User user = userService.getUserByEmail(username);

        // 檢查 email 是否註冊
        if (user == null) {
            logger.warn("email: {} 尚未註冊", username);
            throw new UsernameNotFoundException(username + ": Not found");
        }

        // 添加權限
        List<Role> roleList = new ArrayList<>(user.getRoleSet());
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roleList) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }
}
