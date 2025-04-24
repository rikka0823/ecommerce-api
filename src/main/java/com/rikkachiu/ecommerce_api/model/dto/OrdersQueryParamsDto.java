package com.rikkachiu.ecommerce_api.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdersQueryParamsDto {

    private Integer limit;
    private Integer offset;
}
