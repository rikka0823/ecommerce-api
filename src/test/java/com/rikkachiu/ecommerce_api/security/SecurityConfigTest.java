package com.rikkachiu.ecommerce_api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    // CORS 設定測試
    @Test
    public void corsConfig() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .options("/users/login")
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
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
}