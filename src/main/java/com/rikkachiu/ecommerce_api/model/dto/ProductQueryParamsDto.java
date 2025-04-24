package com.rikkachiu.ecommerce_api.model.dto;

import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductQueryParamsDto {

    private ProductCategory category;
    private String search;
    private String orderBy;
    private String sort;
    private Integer limit;
    private Integer offset;
}
