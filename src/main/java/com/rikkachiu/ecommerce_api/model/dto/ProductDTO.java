package com.rikkachiu.ecommerce_api.model.dto;

import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductDTO {

    @NotNull
    private int productId;

    @NotBlank
    private String productName;

    private ProductCategory category;

    @NotBlank
    private String imageUrl;

    @NotNull
    @Min(value = 1)
    private Integer price;

    @NotNull
    private Integer stock;

    private String description;

    @NotNull
    private Date createdDate;

    @NotNull
    private Date lastModifiedDate;
}
