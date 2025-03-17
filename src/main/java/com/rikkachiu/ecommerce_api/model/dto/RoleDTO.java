package com.rikkachiu.ecommerce_api.model.dto;

import com.rikkachiu.ecommerce_api.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RoleDTO {

    @Email
    @NotBlank
    private String email;

    @NotEmpty
    private Set<Role> roleSet;
}
