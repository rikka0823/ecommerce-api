package com.rikkachiu.ecommerce_api.dao.user;

import com.rikkachiu.ecommerce_api.model.dto.UserDto;
import com.rikkachiu.ecommerce_api.model.pojo.User;

public interface UserDao {

    // 依 id 取得用戶資訊
    User getUserById(Integer userId);

    // 依 email 取得用戶資訊
    User getUserByEmail(String email);

    // 建立帳號
    Integer createUser(UserDto userDTO);

    // 刪除帳號
    void deleteUser(Integer userId);

    // 依 email 更新 refresh_token
    void updateRefreshTokenByEmail(String email, String refreshToken);
}
