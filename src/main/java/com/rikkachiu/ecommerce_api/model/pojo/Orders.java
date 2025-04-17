package com.rikkachiu.ecommerce_api.model.pojo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer orderId;
    private Integer userId;
    private Integer totalAmount;
    private Date createdDate;
    private Date lastModifiedDate;
    private List<OrderItem> orderItemList;
}
