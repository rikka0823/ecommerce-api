package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.dto.PageDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import com.rikkachiu.ecommerce_api.service.orders.OrdersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<PageDTO<Orders>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "5") @Max(25) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") Integer offset
    ) {
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
}
