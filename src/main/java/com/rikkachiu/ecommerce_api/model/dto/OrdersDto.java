package com.rikkachiu.ecommerce_api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto {

    @JsonProperty("buyItemList")
    @NotEmpty
    private List<BuyItemDto> buyItemDtoList;
}
