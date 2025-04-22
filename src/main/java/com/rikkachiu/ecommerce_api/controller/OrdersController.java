package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.dto.PageDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import com.rikkachiu.ecommerce_api.service.orders.OrdersService;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "訂單功能", description = "創建訂單、查詢所有訂單，依不同條件、刪除訂單")
@RestController
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @Operation(summary = "創建", description = "依 userId 創建訂單")
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Orders> createOrders(
            @PathVariable Integer userId,
            @RequestBody @Valid OrdersDTO ordersDTO,
            Authentication authentication,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // 依 email 檢查是否符合當前獲取資源 userId
        userService.checkUserIdByEmail(authentication, jwt, userId);

        // 取得 id 及對應物件
        int orderId = ordersService.createOrders(userId, ordersDTO);
        Orders orders = ordersService.getOrdersById(orderId);

        // 檢查是否為 null
        if (orders == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }

    @Operation(summary = "查詢", description = "查詢所有訂單，依不同條件")
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<PageDTO<Orders>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "5") @Max(25) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            Authentication authentication,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // 依 email 檢查是否符合當前獲取資源 userId
        userService.checkUserIdByEmail(authentication, jwt, userId);

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

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES))
                .body(pageDTO);
    }

    @Operation(summary = "刪除", description = "依 userId 及 orderId 刪除訂單")
    @DeleteMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<?> deleteOrders(
            @PathVariable Integer userId,
            @PathVariable Integer orderId,
            Authentication authentication,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ordersService.deleteOrders(userId, orderId, authentication, jwt);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
