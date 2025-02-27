package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    // 依 id 取得用戶資訊
    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    // 建立帳號
    @Override
    public Integer register(UserDTO userDTO) {
        return userDao.createUser(userDTO);
    }
}
