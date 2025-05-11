package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.CodeDto;
import com.rikkachiu.ecommerce_api.model.dto.RefreshTokenDto;
import com.rikkachiu.ecommerce_api.model.dto.RoleDto;
import com.rikkachiu.ecommerce_api.model.dto.UserDto;
import com.rikkachiu.ecommerce_api.model.pojo.KeycloakToken;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Tag(name = "會員功能",
        description = "建立帳號、登入、依 email 取得用戶資訊、依照 email 更新用戶角色、" +
                "依照 email 更新用戶角色、刪除帳號、生成授權網址、獲取 access token 和 refresh token、" +
                "以 refresh_token 換取 access_token")
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Operation(summary = "建立帳號")
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserDto userDto) {
        // 取得 id 及對應物件
        int userId = userService.register(userDto);
        User user = userService.getUserById(userId);

        // 檢查是否為 null
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "登入")
    @PostMapping("/users/login")
    public ResponseEntity<User> userLogin(Authentication authentication,
                                          @AuthenticationPrincipal Jwt jwt,
                                          HttpServletRequest request) {
        // 記錄登入帳號來源，OAuth2.0、JWT
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            logger.info("使用者： {} 正從 {} 嘗試登入，{}", oidcUser.getEmail(), request.getHeader("User-Agent"), new Date());
        }
        logger.info("使用者： {} 正從 {} 嘗試登入，{}", jwt.getClaims().get("email"), request.getHeader("User-Agent"), new Date());

        return ResponseEntity.status(HttpStatus.OK).body(userService.userLogin(authentication, jwt));
    }

    @Operation(summary = "查詢", description = "依 email 取得用戶資訊")
    @GetMapping("/users/search")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        // 取得 user，檢查是否為 null
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES))
                .body(user);
    }

    @Operation(summary = "更新", description = "依照 email 更新用戶角色")
    @PutMapping("/users/update")
    public ResponseEntity<User> updateUserRolesByEmail(@RequestBody @Valid RoleDto roleDto) {
        // 更新 user，並檢查是否為 null
        User user = userService.updateUserRolesByEmail(roleDto);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "刪除", description = "依照 userId 刪除帳號")
    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId,
                                        Authentication authentication,
                                        @AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(authentication, jwt, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "生成授權網址")
    @GetMapping("/keycloak/buildAuthUrl")
    public ResponseEntity<String> buildAuthUrl() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.buildAuthUrl());
    }

    @Operation(summary = "獲取 access token 和 refresh token")
    @PostMapping("/keycloak/getToken")
    public ResponseEntity<KeycloakToken> getToken(@RequestBody @Valid CodeDto codeDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getToken(codeDto));
    }

    @Operation(summary = "以 refresh_token 換取 access_token")
    @PostMapping("/keycloak/exchangeAccessToken")
    public ResponseEntity<String> exchangeAccessToken(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.exchangeAccessToken(refreshTokenDto));
    }
}
