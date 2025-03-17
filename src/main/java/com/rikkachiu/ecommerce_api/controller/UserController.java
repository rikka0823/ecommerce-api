package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<User> userLogin(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.userLogin(authentication));
    }

    // 依 email 取得用戶資訊
    @GetMapping("/users/search")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        // 取得 user，檢查是否為 null
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // 依照 email 更新用戶角色
    @PutMapping("/users/update")
    public ResponseEntity<User> updateUserRolesByEmail(@RequestBody @Valid RoleDTO roleDTO) {
        // 更新 user，並檢查是否為 null
        User user = userService.updateUserRolesByEmail(roleDTO);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // 刪除帳號
    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId, Authentication authentication) {
        userService.deleteUser(authentication, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
