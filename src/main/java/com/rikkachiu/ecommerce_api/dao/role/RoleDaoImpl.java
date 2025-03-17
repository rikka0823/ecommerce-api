package com.rikkachiu.ecommerce_api.dao.role;

import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.mapper.UserHasRoleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 建立 user 對應角色
    @Override
    public void createUserHasRole(Integer userId, Set<Role> roleSet) {
        // sql 語法與欄位映射
        String sql = """
                INSERT INTO user_has_role (user_id, role_id) VALUES (:userId, :roleId)
                """;
        MapSqlParameterSource[] params = new MapSqlParameterSource[roleSet.size()];
        for (int i = 0; i < roleSet.size(); i++) {
            List<Role> roleList = new ArrayList<>(roleSet);
            int roleId = roleList.get(i).getRoleId();
            params[i] = new MapSqlParameterSource()
                    .addValue("userId", userId)
                    .addValue("roleId", roleId);
        }

        // 批量插入
        namedParameterJdbcTemplate.batchUpdate(sql, params, new GeneratedKeyHolder());
    }

    // 依 id 取得 roles
    @Override
    public Set<Role> getRolesById(Integer userId) {
        // sql 語法與欄位映射
        String sql = """
                SELECT role_id FROM user_has_role WHERE user_id = :userId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        // 建立 roleSet
        List<Integer> roleIds = namedParameterJdbcTemplate.query(sql, params, new UserHasRoleRowMapper());
        Set<Role> roleSet = new HashSet<>();
        for (Integer id : roleIds) {
            roleSet.add(Role.values()[id - 1]);
        }

        return roleSet;
    }

    // 依 email 刪除 roles
    @Override
    public void deleteRolesById(String email) {
        // sql 語法與欄位映射
        String sql = """
                DELETE FROM user_has_role
                WHERE user_id IN (
                    SELECT user_id
                    FROM `user`
                    WHERE email = :email
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
