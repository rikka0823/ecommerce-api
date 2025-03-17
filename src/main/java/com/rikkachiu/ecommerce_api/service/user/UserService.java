package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.springframework.security.core.Authentication;

public interface UserService {

    // 依 email 取得用戶資訊
    User getUserByEmail(String email);

    // 依 email 檢查是否符合當前獲取資源 userId
    void checkUserIdByEmail(Authentication authentication, Integer userId);

    // 依 id 取得用戶資訊
    User getUserById(Integer userId);

    // 建立帳號
    Integer register(UserDTO userDTO);

    // 登入
    User userLogin(Authentication authentication);

    // 依照 email 更新用戶角色
    User updateUserRolesByEmail(RoleDTO roleDTO);

    // 刪除帳號
    void deleteUser(Authentication authentication, Integer userId);
}
