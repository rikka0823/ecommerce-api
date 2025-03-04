package com.rikkachiu.ecommerce_api.service.orders;

import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;

import java.util.List;

public interface OrdersService {

    // 創建訂單
    Integer createOrders(Integer userId, OrdersDTO ordersDTO);

    //取得訂單
    Orders getOrdersById(Integer orderId);

    // 查詢訂單總數
    Integer getOrdersCount(Integer userId);

    // 查詢所有訂單，依不同條件
    List<Orders> getOrders(Integer userId, OrdersQueryParamsDTO ordersQueryParamsDTO);
}
