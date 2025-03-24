package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.dto.PageDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import com.rikkachiu.ecommerce_api.service.orders.OrdersService;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    // 創建訂單
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Orders> createOrders(
            @PathVariable Integer userId,
            @RequestBody @Valid OrdersDTO ordersDTO,
            Authentication authentication
    ) {
        // 依 email 檢查是否符合當前獲取資源 userId
        userService.checkUserIdByEmail(authentication, userId);

        // 取得 id 及對應物件
        int orderId = ordersService.createOrders(userId, ordersDTO);
        Orders orders = ordersService.getOrdersById(orderId);

        // 檢查是否為 null
        if (orders == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }

    // 查詢所有訂單，依不同條件
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<PageDTO<Orders>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "5") @Max(25) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            Authentication authentication
    ) {
        // 依 email 檢查是否符合當前獲取資源 userId
        userService.checkUserIdByEmail(authentication, userId);

        // 將查詢參數封裝
        OrdersQueryParamsDTO ordersQueryParamsDTO = OrdersQueryParamsDTO.builder()
                .limit(limit)
                .offset(offset)
                .build();

        // 將 返回資料封裝在 PageDTO<Orders>
        PageDTO<Orders> pageDTO = new PageDTO<>();
        pageDTO.setLimit(limit);
        pageDTO.setOffset(offset);
        pageDTO.setTotal(ordersService.getOrdersCount(userId));
        pageDTO.setResults(ordersService.getOrders(userId, ordersQueryParamsDTO));

        return ResponseEntity.status(HttpStatus.OK).body(pageDTO);
    }

    // 刪除訂單
    @DeleteMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<?> deleteOrders(
            @PathVariable Integer userId,
            @PathVariable Integer orderId,
            Authentication authentication
    ) {
        ordersService.deleteOrders(userId, orderId, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
