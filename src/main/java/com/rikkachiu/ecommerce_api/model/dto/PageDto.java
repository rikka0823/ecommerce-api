package com.rikkachiu.ecommerce_api.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageDto<T> {

    private Integer limit;
    private Integer offset;
    private Integer total;
    private List<T> results;
}
