package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 建立帳號
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserDTO userDTO) {
        // 取得 id 及對應物件
        int userId = userService.register(userDTO);
        User user = userService.getUserById(userId);

        // 檢查是否為 null
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // 登入
    @PostMapping("/users/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(userDTO));
    }
}
