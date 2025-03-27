package com.rikkachiu.ecommerce_api.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CodeDTO {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String code;
}
