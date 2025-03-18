package com.rikkachiu.ecommerce_api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@AutoConfigureMockMvc
@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // CORS 設定測試
    @Test
    public void corsConfig() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .options("/users/login")
                .with(httpBasic("test1@gmail.com", "111"))
                .header("Access-Control-Request-Method", "POST")
                .header("Origin", "http://localhost:8080");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(header().exists("Access-Control-ALLOW-Methods"))
                .andExpect(header().string("Access-Control-ALLOW-Methods", "POST,GET,UPDATE,DELETE"))
                .andExpect(header().exists("Access-Control-ALLOW-Origin"))
                .andExpect(header().string("Access-Control-ALLOW-Origin", "http://localhost:8080"));

    }
}