package com.rikkachiu.ecommerce_api.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenDto {

    @NotEmpty
    String refreshToken;
}
