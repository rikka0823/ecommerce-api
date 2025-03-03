package com.rikkachiu.ecommerce_api.model.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItem {

    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Integer amount;
    private String productName;
    private String imageUrl;
}
