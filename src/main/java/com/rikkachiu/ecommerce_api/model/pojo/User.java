package com.rikkachiu.ecommerce_api.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class User {

    private Integer userId;
    private String email;

    @JsonIgnore
    private String password;

    private Date createdDate;
    private Date lastModifiedDate;
}
