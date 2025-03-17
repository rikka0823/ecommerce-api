package com.rikkachiu.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.RoleDTO;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

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

    // 註冊帳號，email 格式錯誤狀態
    @Transactional
    @Test
    public void registerByEmailAlreadyExist() throws Exception {
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

        // 驗證第一次註冊
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isCreated());

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 註冊
    private void register(UserDTO userDTO) throws Exception {
        // 建立 json 內容
        String json = objectMapper.writeValueAsString(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(201));
    }

    // 登入，成功狀態
    @Test
    public void loginOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .with(httpBasic("test3@gmail.com", "333"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test3@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    // 登入，email 格式錯誤狀態
    @Transactional
    @Test
    public void loginByInvalidEmailFormat() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ROLE_ADMIN);
        userDTO.setRoleSet(roleSet);

        // 註冊
        register(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .with(httpBasic("testgmail.com", "123"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // 登入，email 不存在狀態
    @Test
    public void loginByEmailNotExists() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .with(httpBasic("test@gmail.com", "111"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // 登入，密碼錯誤狀態
    @Transactional
    @Test
    public void loginByWrongPassword() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ROLE_ADMIN);
        userDTO.setRoleSet(roleSet);

        // 註冊
        register(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .with(httpBasic("test@gmail.com", "12"));

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
                .with(httpBasic("test1@gmail.com", "111"));

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

    // 更新會員角色，403
    @Transactional
    @Test
    public void updateUserRolesByEmailOnForbidden() throws Exception {
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
                .with(httpBasic("test2@gmail.com", "222"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // 依 email 取得用戶資訊，200
    @Test
    public void getUserByEmailOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/search")
                .param("email", "test2@gmail.com")
                .with(httpBasic("test1@gmail.com", "111"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }

    // 依 email 取得用戶資訊，403
    @Test
    public void getUserByEmailOnForbidden() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/search")
                .param("email", "test2@gmail.com")
                .with(httpBasic("test2@gmail.com", "222"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // 刪除帳號，204
    @Transactional
    @Test
    public void deleteUserOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/delete", 1)
                .with(httpBasic("test1@gmail.com", "111"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // 刪除帳號，400
    @Transactional
    @Test
    public void deleteUserOnForbidden() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/delete", 1)
                .with(httpBasic("test2@gmail.com", "222"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
