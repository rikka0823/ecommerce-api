package com.rikkachiu.ecommerce_api.mapper;

import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        //  user 欄位與物件映射
        User user = User.builder()
                .userId(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .createdDate(rs.getTimestamp("created_date"))
                .lastModifiedDate(rs.getTimestamp("last_modified_date"))
                .providerUserId(rs.getString("provider_user_id"))
                .provider(rs.getString("provider"))
                .build();

        return user;
    }
}
