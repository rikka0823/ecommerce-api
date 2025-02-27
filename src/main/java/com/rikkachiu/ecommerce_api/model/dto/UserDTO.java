package com.rikkachiu.ecommerce_api.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
