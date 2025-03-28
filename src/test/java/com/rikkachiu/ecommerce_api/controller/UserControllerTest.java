package com.rikkachiu.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.CodeDTO;
import com.rikkachiu.ecommerce_api.model.dto.RefreshTokenDTO;
import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 註冊帳號，成功狀態
    @Transactional
    @Test
    public void registerOnSuccess() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ROLE_ADMIN);
        userDTO.setRoleSet(roleSet);
        String json = objectMapper.writeValueAsString(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
        assertNotEquals(userDao.getUserByEmail(userDTO.getEmail()).getPassword(), userDTO.getPassword());
    }

    // 註冊帳號，email 格式錯誤狀態
    @Transactional
    @Test
    public void registerByInvalidEmailFormat() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("testgmail.com");
        userDTO.setPassword("123");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ROLE_ADMIN);
        userDTO.setRoleSet(roleSet);
        String json = objectMapper.writeValueAsString(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 登入，成功狀態
    @Test
    public void loginOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test1@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    // 登入，email，401
    @Test
    public void loginOnFail() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .header("Authorization", "Bearer " + "test");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // 更新會員角色，200
    @Transactional
    @Test
    public void updateUserRolesByEmailOnSuccess() throws Exception {
        // 建立 json 內容
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setEmail("test3@gmail.com");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ROLE_ADMIN);
        roleDTO.setRoleSet(roleSet);
        String json = objectMapper.writeValueAsString(roleDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test3@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()))
                .andExpect(jsonPath("$.roleSet", notNullValue()));
    }

    // 更新會員角色，401
    @Transactional
    @Test
    public void updateUserRolesByEmailOnFail() throws Exception {
        // 建立 json 內容
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setEmail("test3@gmail.com");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ROLE_ADMIN);
        roleDTO.setRoleSet(roleSet);
        String json = objectMapper.writeValueAsString(roleDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer " + "test");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // 依 email 取得用戶資訊，200
    @Test
    public void getUserByEmailOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/search")
                .param("email", "test2@gmail.com")
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }

    // 依 email 取得用戶資訊，401
    @Test
    public void getUserByEmailOnFail() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/search")
                .param("email", "test2@gmail.com")
                .header("Authorization", "Bearer " + "test");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // 刪除帳號，204
    @Transactional
    @Test
    public void deleteUserOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/delete", 1)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // 刪除帳號，401
    @Transactional
    @Test
    public void deleteUserOnFail() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/delete", 1)
                .header("Authorization", "Bearer " + "test");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // 生成授權網址
    @Test
    public void buildAuthUrl() throws Exception {
        // 設定請求路徑
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/keycloak/buildAuthUrl");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    // 獲取 access token 和 refresh token，400
    @Transactional
    @Test
    public void getToken() throws Exception {
        // 設定請求路徑、參數
        CodeDTO codeDTO = new CodeDTO();
        codeDTO.setCode("test");
        codeDTO.setEmail("test1@gmail.com");
        String json = objectMapper.writeValueAsString(codeDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/keycloak/getToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 以 refresh_token 換取 access_token
    @Test
    public void exchangeAccessToken() throws Exception {
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken("test");
        String json = objectMapper.writeValueAsString(refreshTokenDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/keycloak/exchangeAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
