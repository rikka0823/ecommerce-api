package com.rikkachiu.ecommerce_api.model.pojo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Integer amount;
    private String productName;
    private String imageUrl;
}
