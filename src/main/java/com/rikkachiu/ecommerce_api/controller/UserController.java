package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.CodeDTO;
import com.rikkachiu.ecommerce_api.model.dto.RefreshTokenDTO;
import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.KeycloakToken;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    public ResponseEntity<User> userLogin(Authentication authentication,
                                          @AuthenticationPrincipal Jwt jwt,
                                          HttpServletRequest request) {
        // 紀錄登入帳號來源，OAuth2.0、JWT
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            logger.info("使用者： {} 正從 {} 嘗試登入，{}", oidcUser.getEmail(), request.getHeader("User-Agent"), new Date());
        }
        logger.info("使用者： {} 正從 {} 嘗試登入，{}", jwt.getClaims().get("email"), request.getHeader("User-Agent"), new Date());

        return ResponseEntity.status(HttpStatus.OK).body(userService.userLogin(authentication, jwt));
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
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId,
                                        Authentication authentication,
                                        @AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(authentication, jwt, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 生成授權網址
    @GetMapping("/keycloak/buildAuthUrl")
    public ResponseEntity<String> buildAuthUrl() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.buildAuthUrl());
    }


    // 獲取 access token 和 refresh token
    @PostMapping("/keycloak/getToken")
    public ResponseEntity<KeycloakToken> getToken(@RequestBody @Valid CodeDTO codeDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getToken(codeDTO));
    }

    // 以 refresh_token 換取 access_token
    @PostMapping("/keycloak/exchangeAccessToken")
    public ResponseEntity<String> exchangeAccessToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.exchangeAccessToken(refreshTokenDTO));
    }
}
