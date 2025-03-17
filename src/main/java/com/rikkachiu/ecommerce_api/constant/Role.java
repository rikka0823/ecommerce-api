package com.rikkachiu.ecommerce_api.constant;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_ADMIN(1),
    ROLE_SELLER(2),
    ROLE_CUSTOMER(3);

    private final int roleId;

    Role(int roleId) {
        this.roleId = roleId;
    }
}
