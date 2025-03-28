package com.rikkachiu.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkachiu.ecommerce_api.model.dto.BuyItemDTO;
import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrdersControllerTest {

    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 創建訂單，201
    @Transactional
    @Test
    public void createOrdersOnSuccess() throws Exception {
        // 建立 json 內容
        List<BuyItemDTO> buyItemDTOList = new ArrayList<>();
        BuyItemDTO buyItemDTO = new BuyItemDTO();
        buyItemDTO.setProductId(1);
        buyItemDTO.setQuantity(6);
        buyItemDTOList.add(buyItemDTO);
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBuyItemDTOList(buyItemDTOList);
        String json = objectMapper.writeValueAsString(ordersDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 13)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", equalTo(1)))
                .andExpect(jsonPath("$.userId", equalTo(13)))
                .andExpect(jsonPath("$.totalAmount", notNullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()))
                .andExpect(jsonPath("$.orderItemList", notNullValue()));
    }

    // 創建訂單，userId不存在，400
    @Transactional
    @Test
    public void createOrdersOnUserIdNotExists() throws Exception {
        // 建立 json 內容
        List<BuyItemDTO> buyItemDTOList = new ArrayList<>();
        BuyItemDTO buyItemDTO = new BuyItemDTO();
        buyItemDTO.setProductId(1);
        buyItemDTO.setQuantity(6);
        buyItemDTOList.add(buyItemDTO);
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBuyItemDTOList(buyItemDTOList);
        String json = objectMapper.writeValueAsString(ordersDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 130)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 創建訂單，庫存不足，400
    @Transactional
    @Test
    public void createOrdersOnStockNotEnough() throws Exception {
        // 建立 json 內容
        List<BuyItemDTO> buyItemDTOList = new ArrayList<>();
        BuyItemDTO buyItemDTO = new BuyItemDTO();
        buyItemDTO.setProductId(1);
        buyItemDTO.setQuantity(60);
        buyItemDTOList.add(buyItemDTO);
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBuyItemDTOList(buyItemDTOList);
        String json = objectMapper.writeValueAsString(ordersDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 13)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 創建訂單，productId不存在，400
    @Transactional
    @Test
    public void createOrdersOnProductIdNotExists() throws Exception {
        // 建立 json 內容
        List<BuyItemDTO> buyItemDTOList = new ArrayList<>();
        BuyItemDTO buyItemDTO = new BuyItemDTO();
        buyItemDTO.setProductId(100);
        buyItemDTO.setQuantity(6);
        buyItemDTOList.add(buyItemDTO);
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBuyItemDTOList(buyItemDTOList);
        String json = objectMapper.writeValueAsString(ordersDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 13)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 創建訂單，無效參數，400
    @Transactional
    @Test
    public void createOrdersOnBadRequest() throws Exception {
        // 建立 json 內容
        List<BuyItemDTO> buyItemDTOList = new ArrayList<>();
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBuyItemDTOList(buyItemDTOList);
        String json = objectMapper.writeValueAsString(ordersDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 13)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 查詢訂單，200
    @Test
    public void getOrdersOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 13)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", equalTo(5)))
                .andExpect(jsonPath("$.offset", equalTo(0)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.results", notNullValue()));
    }

    // 查詢訂單，pagination 篩選
    @Test
    public void getOrdersByPagination() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 13)
                .param("limit", "3")
                .param("offset", "2")
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", equalTo(3)))
                .andExpect(jsonPath("$.offset", equalTo(2)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.results", notNullValue()));
    }

    // 查詢訂單，400
    @Test
    public void getOrdersOnBadRequest() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 130)
                .param("limit", "3")
                .param("offset", "2")
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(400));
    }

    // 刪除訂單，204
    @Transactional
    @Test
    public void deleteOrdersOnBadRequest() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}/orders/{orderId}", 13, 14)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(204));
    }
}