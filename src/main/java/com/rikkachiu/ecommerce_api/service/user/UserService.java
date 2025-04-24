package com.rikkachiu.ecommerce_api.service.user;

import com.rikkachiu.ecommerce_api.model.dto.CodeDto;
import com.rikkachiu.ecommerce_api.model.dto.RefreshTokenDto;
import com.rikkachiu.ecommerce_api.model.dto.RoleDto;
import com.rikkachiu.ecommerce_api.model.dto.UserDto;
import com.rikkachiu.ecommerce_api.model.pojo.KeycloakToken;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    // 依 email 取得用戶資訊
    User getUserByEmail(String email);

    // 依 email 檢查是否符合當前獲取資源 userId
    void checkUserIdByEmail(Authentication authentication, Jwt jwt, Integer userId);

    // 依 id 取得用戶資訊
    User getUserById(Integer userId);

    // 建立帳號
    Integer register(UserDto userDTO);

    // 登入
    User userLogin(Authentication authentication, Jwt jwt);

    // 依照 email 更新用戶角色
    User updateUserRolesByEmail(RoleDto roleDTO);

    // 刪除帳號
    void deleteUser(Authentication authentication, Jwt jwt, Integer userId);

    // 生成授權網址
    String buildAuthUrl();

    // 獲取 access token 和 refresh token
    KeycloakToken getToken(CodeDto codeDTO);

    // 以 refresh_token 換取 access_token
    String exchangeAccessToken(RefreshTokenDto refreshTokenDTO);
}
