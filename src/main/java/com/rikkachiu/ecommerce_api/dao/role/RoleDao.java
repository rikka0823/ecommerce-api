package com.rikkachiu.ecommerce_api.dao.role;

import com.rikkachiu.ecommerce_api.constant.Role;

import java.util.Set;

public interface RoleDao {

    // 建立 user 對應角色
    void createUserHasRole(Integer userId, Set<Role> roleSet);

    // 依 id 取得 roles
    Set<Role> getRolesById(Integer userId);

    // 依 email 刪除 roles
    void deleteRolesById(String email);
}
