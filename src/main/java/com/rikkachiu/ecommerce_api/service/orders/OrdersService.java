package com.rikkachiu.ecommerce_api.service.orders;

import com.rikkachiu.ecommerce_api.model.dto.OrdersDto;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDto;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface OrdersService {

    // 創建訂單
    Integer createOrders(Integer userId, OrdersDto ordersDTO);

    //取得訂單
    Orders getOrdersById(Integer orderId);

    // 查詢訂單總數
    Integer getOrdersCount(Integer userId);

    // 查詢所有訂單，依不同條件
    List<Orders> getOrders(Integer userId, OrdersQueryParamsDto ordersQueryParamsDTO);

    // 刪除訂單
    void deleteOrders(Integer userId, Integer orderId, Authentication authentication, Jwt jwt);
}
