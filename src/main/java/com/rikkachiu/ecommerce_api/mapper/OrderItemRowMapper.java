package com.rikkachiu.ecommerce_api.mapper;

import com.rikkachiu.ecommerce_api.model.pojo.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItem> {

    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        // OrderItem 物件映射
        OrderItem orderItem = OrderItem.builder()
                .orderItemId(rs.getInt("order_item_id"))
                .orderId(rs.getInt("order_id"))
                .productId(rs.getInt("product_id"))
                .quantity(rs.getInt("quantity"))
                .amount(rs.getInt("amount"))
                .productName(rs.getString("product_name"))
                .imageUrl(rs.getString("image_url"))
                .build();

        return orderItem;
    }
}
