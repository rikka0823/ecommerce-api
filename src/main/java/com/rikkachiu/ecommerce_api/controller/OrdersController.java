package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import com.rikkachiu.ecommerce_api.service.orders.OrdersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ProductDao productDao;

    // 創建訂單
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Orders> createOrders(@PathVariable Integer userId,
                                         @RequestBody @Valid OrdersDTO ordersDTO) {
        // 取得 id 及對應物件
        int orderId = ordersService.createOrders(userId, ordersDTO);
        Orders orders = ordersService.getOrdersById(orderId);

        // 檢查是否為 null
        if (orders == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }
}
