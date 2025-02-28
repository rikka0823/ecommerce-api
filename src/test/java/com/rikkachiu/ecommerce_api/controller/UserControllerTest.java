package com.rikkachiu.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    @Transactional
    @Test
    public void loginOnSuccess() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123");
        String json = objectMapper.writeValueAsString(userDTO);

        // 註冊
        register(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test@gmail.com")))
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

        // 註冊
        register(userDTO);

        // 設定 email 格式錯誤
        userDTO.setEmail("test");
        String json = objectMapper.writeValueAsString(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 登入，email 不存在狀態
    @Transactional
    @Test
    public void loginByEmailNotExists() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123");
        String json = objectMapper.writeValueAsString(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 登入，密碼錯誤狀態
    @Transactional
    @Test
    public void loginByWrongPassword() throws Exception {
        // 建立 json 內容
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("123");

        // 註冊
        register(userDTO);

        // 設定密碼錯誤
        userDTO.setPassword("124");
        String json = objectMapper.writeValueAsString(userDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}