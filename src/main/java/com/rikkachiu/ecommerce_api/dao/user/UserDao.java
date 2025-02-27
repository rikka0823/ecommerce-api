package com.rikkachiu.ecommerce_api.dao.user;

import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;

public interface UserDao {

    // 依 id 取得用戶資訊
    User getUserById(Integer userId);

    // 依 email 取得用戶資訊
    User getUserByEmail(String email);

    // 建立帳號
    Integer createUser(UserDTO userDTO);
}
