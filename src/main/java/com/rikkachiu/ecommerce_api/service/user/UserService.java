package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;

public interface UserService {

    // 依 id 取得用戶資訊
    User getUserById(Integer userId);

    // 建立帳號
    Integer register(UserDTO userDTO);
}
