package com.rikkachiu.ecommerce_api.model.dto;

import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
public class ProductDto {

    @NotBlank
    private String productName;

    @NotNull
    private ProductCategory category;

    @NotBlank
    private String imageUrl;

    @NotNull
    @Min(value = 1)
    private Integer price;

    @NotNull
    @Min(value = 0)
    private Integer stock;

    private String description;
}
