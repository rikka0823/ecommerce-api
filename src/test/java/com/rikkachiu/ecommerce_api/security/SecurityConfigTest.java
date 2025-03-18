package com.rikkachiu.ecommerce_api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // CORS 設定測試
    @Test
    public void corsConfig() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .options("/users/login")
                .with(httpBasic("test1@gmail.com", "111"))
                .header("Access-Control-Request-Method", "POST")
                .header("Origin", "http://localhost:8080");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(header().exists("Access-Control-ALLOW-Methods"))
                .andExpect(header().string("Access-Control-ALLOW-Methods", "POST,GET,UPDATE,DELETE"))
                .andExpect(header().exists("Access-Control-ALLOW-Origin"))
                .andExpect(header().string("Access-Control-ALLOW-Origin", "http://localhost:8080"));
    }

    // 帶有 CSRF token，204
    @Transactional
    @Test
    public void deleteUserWithCsrfToken() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/delete", 1)
                .with(httpBasic("test1@gmail.com", "111"))
                .with(csrf());

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(204));
    }

    // 不帶有 CSRF token，403
    @Transactional
    @Test
    public void deleteUserNoCsrfToken() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/delete", 1)
                .with(httpBasic("test1@gmail.com", "111"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(403));
    }

    // 註冊不帶有 CSRF token，200
    @Transactional
    @Test
    public void loginNoCsrfToken() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .with(httpBasic("test1@gmail.com", "111"));

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(200));
    }
}