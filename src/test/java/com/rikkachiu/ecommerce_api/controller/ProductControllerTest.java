package com.rikkachiu.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN;

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 查詢所有產品，無任何參數
    @Test
    public void getProducts() throws Exception {
        // 設定請求路徑
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(5)));
    }

    // 查詢所有產品，filtering 篩選
    @Test
    public void getProductsByFiltering() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("category", "FOOD")
                .param("search", "蘋果");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(3)));
    }

    // 查詢所有產品，sorting 篩選
    @Test
    public void getProductsBySorting() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("orderBy", "PRICE")
                .param("sort", "ASC");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results[0].price", equalTo(10)))
                .andExpect(jsonPath("$.results[1].price", equalTo(30)))
                .andExpect(jsonPath("$.results[2].price", equalTo(300)))
                .andExpect(jsonPath("$.results", hasSize(5)));
    }


    // 查詢所有產品，pagination 篩選
    @Test
    public void getProductsByPagination() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("limit", "3")
                .param("offset", "2");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(3)))
                .andExpect(jsonPath("$.results[0].productId", equalTo(6)))
                .andExpect(jsonPath("$.results[1].productId", equalTo(7)))
                .andExpect(jsonPath("$.results[2].productId", equalTo(5)));
    }

    // 依 id 查詢商品，200
    @Test
    public void getProductByIdOnSuccess() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}", 4);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", equalTo(4)))
                .andExpect(jsonPath("$.productName", equalTo("Toyota")))
                .andExpect(jsonPath("$.category", equalTo("CAR")))
                .andExpect(jsonPath("$.imageUrl", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.stock", notNullValue()))
                .andExpect(jsonPath("description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    // 依 id 查詢商品，404
    @Test
    public void getProductByIdOnNotFound() throws Exception {
        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}", 40);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // 新增商品，201
    @Transactional
    @Test
    public void createProductOnSuccess() throws Exception {
        // 建立 json 內容
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("apple_book");
        productDTO.setCategory(ProductCategory.E_BOOK);
        productDTO.setStock(1);
        productDTO.setPrice(10);
        productDTO.setImageUrl("http://book.com");
        String json = objectMapper.writeValueAsString(productDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.productId", equalTo(8)))
                .andExpect(jsonPath("$.productName", equalTo("apple_book")))
                .andExpect(jsonPath("$.category", equalTo("E_BOOK")))
                .andExpect(jsonPath("$.imageUrl", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.stock", notNullValue()))
                .andExpect(jsonPath("description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    // 新增商品，400
    @Transactional
    @Test
    public void createProductOnBadRequest() throws Exception {
        // 建立 json 內容
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("apple_book");
        productDTO.setCategory(ProductCategory.E_BOOK);
        productDTO.setStock(null);
        productDTO.setPrice(10);
        productDTO.setImageUrl("http://book.com");
        String json = objectMapper.writeValueAsString(productDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(400));
    }

    // 新增商品，401
    @Transactional
    @Test
    public void createProductOnForbidden() throws Exception {
        // 建立 json 內容
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("apple_book");
        productDTO.setCategory(ProductCategory.E_BOOK);
        productDTO.setStock(1);
        productDTO.setPrice(10);
        productDTO.setImageUrl("http://book.com");
        String json = objectMapper.writeValueAsString(productDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + "TEST_ACCESS_TOKEN")
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(401));
    }

    // 依 id 更新商品，200
    @Transactional
    @Test
    public void updateProductOnSuccess() throws Exception {
        // 建立 json 內容
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("apple_book");
        productDTO.setCategory(ProductCategory.E_BOOK);
        productDTO.setStock(2);
        productDTO.setPrice(10);
        productDTO.setImageUrl("http://book.com");
        String json = objectMapper.writeValueAsString(productDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(200));
    }

    // 依 id 更新商品，404
    @Transactional
    @Test
    public void updateProductOnNotFound() throws Exception {
        // 建立 json 內容
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("apple_book");
        productDTO.setCategory(ProductCategory.E_BOOK);
        productDTO.setStock(2);
        productDTO.setPrice(10);
        productDTO.setImageUrl("http://book.com");
        String json = objectMapper.writeValueAsString(productDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 9)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(404));
    }

    // 依 id 更新商品，401
    @Transactional
    @Test
    public void updateProductOnForbidden() throws Exception {
        // 建立 json 內容
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("apple_book");
        productDTO.setCategory(ProductCategory.E_BOOK);
        productDTO.setStock(2);
        productDTO.setPrice(10);
        productDTO.setImageUrl("http://book.com");
        String json = objectMapper.writeValueAsString(productDTO);

        // 設定請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + "TEST_ACCESS_TOKEN")
                .content(json);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(401));
    }

    // 依 id 刪除商品，204
    @Transactional
    @Test
    public void deleteProductById() throws Exception {
        // 設請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}", 100)
                .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN);

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // 依 id 刪除商品，401
    @Transactional
    @Test
    public void deleteProductByIdOnForbidden() throws Exception {
        // 設請求路徑、參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}", 100)
                .header("Authorization", "Bearer " + "TEST_ACCESS_TOKEN");

        // 驗證返回內容
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(401));
    }
}