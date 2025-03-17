package com.rikkachiu.ecommerce_api.dao.user;

import com.rikkachiu.ecommerce_api.mapper.UserRowMapper;
import com.rikkachiu.ecommerce_api.model.dto.UserDTO;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 依 id 取得用戶資訊
    @Override
    public User getUserById(Integer userId) {
        // sql 語法與欄位映射
        String sql = "SELECT user_id, email, password, created_date, last_modified_date FROM `user` WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        // 取得映射物件 list，並檢查是否為空
        List<User> user = namedParameterJdbcTemplate
                .query(sql, params, new UserRowMapper());
        if (user.isEmpty()) {
            return null;
        }

        return user.get(0);
    }

    // 依 email 取得用戶資訊
    @Override
    public User getUserByEmail(String email) {
        // sql 語法與欄位映射
        String sql = "SELECT user_id, email, password, created_date, last_modified_date FROM `user` WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);

        // 取得映射物件 list，並檢查是否為空
        List<User> user = namedParameterJdbcTemplate
                .query(sql, params, new UserRowMapper());
        if (user.isEmpty()) {
            return null;
        }

        return user.get(0);
    }

    // 建立帳號
    @Override
    public Integer createUser(UserDTO userDTO) {
        // sql 語法與欄位映射
        String sql = "INSERT INTO `user` (email, password, created_date, last_modified_date) " +
                "VALUES (:email, :password, :createdDate, :lastModifiedDate);";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", userDTO.getEmail())
                .addValue("password", userDTO.getPassword())
                .addValue("createdDate", new Date())
                .addValue("lastModifiedDate", new Date());

        // 建立帳號，並取得帳號 id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
        int id = keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;

        return id;
    }

    // 刪除帳號
    @Override
    public void deleteUser(Integer userId) {
        // sql 語法與欄位映射
        String sql = """
                DELETE FROM `user` WHERE user_id = :userId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
