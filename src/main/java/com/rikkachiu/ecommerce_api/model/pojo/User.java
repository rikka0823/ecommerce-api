package com.rikkachiu.ecommerce_api.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rikkachiu.ecommerce_api.constant.Role;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String email;

    @JsonIgnore
    private String password;

    private Date createdDate;
    private Date lastModifiedDate;
    private Set<Role> roleSet;
    private String providerUserId;
    private String provider;
    private String refreshToken;
}
