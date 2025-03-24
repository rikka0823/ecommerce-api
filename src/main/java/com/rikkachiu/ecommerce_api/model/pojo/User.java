package com.rikkachiu.ecommerce_api.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rikkachiu.ecommerce_api.constant.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Builder
public class User {

    private Integer userId;
    private String email;

    @JsonIgnore
    private String password;

    private Date createdDate;
    private Date lastModifiedDate;
    private Set<Role> roleSet;
    private String providerUserId;
    private String provider;
}
