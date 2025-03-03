package com.rikkachiu.ecommerce_api.dao.orders;

import com.rikkachiu.ecommerce_api.model.pojo.OrderItem;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;

import java.util.List;

public interface OrdersDao {

    // 創建訂單
    Integer createOrders(Integer userId, Integer totalAmount);

    // 批量創建商品明細
    void createOrderItem(Integer orderId, List<OrderItem> orderItemList);

    // 依 orderId 取得訂單
    Orders getOrdersById(Integer orderId);

    // 依 orderId 取得商品明細
    List<OrderItem> getOrderItemsById(Integer orderId);
}
