package com.rikkachiu.ecommerce_api.dao.orders;

import com.rikkachiu.ecommerce_api.mapper.OrderItemRowMapper;
import com.rikkachiu.ecommerce_api.mapper.OrdersRowMapper;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.pojo.OrderItem;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class OrdersDaoImpl implements OrdersDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 創建訂單
    @Override
    public Integer createOrders(Integer userId, Integer totalAmount) {
        // sql 語法與欄位映射
        String sql = "INSERT INTO orders (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:userId, :totalAmount, :createdDate, :lastModifiedDate)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("totalAmount", totalAmount)
                .addValue("createdDate", new Date())
                .addValue("lastModifiedDate", new Date());

        // 創建訂單，並取得訂單 id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
        int id = keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;

        return id;
    }

    // 批量創建商品明細
    @Override
    public void createOrderItem(Integer orderId, List<OrderItem> orderItemList) {
        // sql 語法與欄位映射
        String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount) " +
                "VALUES (:orderId, :productId, :quantity, :amount)";
        MapSqlParameterSource[] params = new MapSqlParameterSource[orderItemList.size()];
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            params[i] = new MapSqlParameterSource()
                    .addValue("orderId", orderId)
                    .addValue("productId", orderItem.getProductId())
                    .addValue("quantity", orderItem.getQuantity())
                    .addValue("amount", orderItem.getAmount());
        }

        // 批量創建商品明細
        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    // 依 orderId 取得訂單
    @Override
    public Orders getOrdersById(Integer orderId) {
        // sql 語法與欄位映射
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM orders WHERE order_id = :orderId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderId", orderId);

        // 取得映射物件 list，並檢查是否為空
        List<Orders> ordersList = namedParameterJdbcTemplate.query(sql, params,
                new OrdersRowMapper());
        if (ordersList.isEmpty()) {
            return null;
        }

        return ordersList.get(0);
    }

    // 依 orderId 取得商品明細
    @Override
    public List<OrderItem> getOrderItemsById(Integer orderId) {
        // sql 語法與欄位映射
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.amount, p.product_name, p.image_url " +
                "FROM order_item AS oi " +
                "LEFT JOIN product AS p " +
                "ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = :orderId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderId", orderId);

        return namedParameterJdbcTemplate.query(sql, params, new OrderItemRowMapper());
    }

    //新增篩選條件 sql
    private void addFilteringSql(Integer userId,  StringBuilder sql,
                                 MapSqlParameterSource params) {
        sql.append(" AND user_id = :userId");
        params.addValue("userId", userId);
    }

    // 查詢訂單總數
    @Override
    public Integer getOrdersCount(Integer userId) {
        // sql 語法與欄位映射
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        //新增篩選條件 sql
        addFilteringSql(userId, sql, params);

        // 取得商品總數
        Integer count = namedParameterJdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        if (count == null) {
            return 0;
        }

        return count;
    }

    // 查詢所有訂單，依不同條件
    @Override
    public List<Orders> getOrders(Integer userId, OrdersQueryParamsDTO ordersQueryParamsDTO) {
        // sql 語法與欄位映射
        StringBuilder sql = new StringBuilder("SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM orders " +
                "WHERE 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        // 新增篩選條件 sql
        addFilteringSql(userId, sql, params);

        // 依據 orderBy, sort 排序
        sql.append(" ORDER BY last_modified_date DESC")
                .append(" LIMIT ")
                .append(":limit")
                .append(" OFFSET ")
                .append(":offset");
        params.addValue("limit",ordersQueryParamsDTO.getLimit())
                .addValue("offset", ordersQueryParamsDTO.getOffset());

        return namedParameterJdbcTemplate.query(sql.toString(), params, new OrdersRowMapper());
    }
}
