package com.rikkachiu.ecommerce_api.mapper;

import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersRowMapper implements RowMapper<Orders> {

    @Override
    public Orders mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Orders 物件映射
        Orders orders = Orders.builder()
                .orderId(rs.getInt("order_id"))
                .userId(rs.getInt("user_id"))
                .totalAmount(rs.getInt("total_amount"))
                .createdDate(rs.getTimestamp("created_date"))
                .lastModifiedDate(rs.getTimestamp("last_modified_date"))
                .build();

        return orders;
    }
}
