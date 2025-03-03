package com.rikkachiu.ecommerce_api.service.orders;

import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;

public interface OrdersService {

    // 創建訂單
    Integer createOrders(Integer userId, OrdersDTO ordersDTO);

    //取得訂單
    Orders getOrdersById(Integer orderId);
}
