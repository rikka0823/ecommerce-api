package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
        // 檢查 email 是否已存在
        if (userDao.getUserByEmail(userDTO.getEmail()) != null) {
            logger.warn("email: {} 已存在", userDTO.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return userDao.createUser(userDTO);
    }
}
