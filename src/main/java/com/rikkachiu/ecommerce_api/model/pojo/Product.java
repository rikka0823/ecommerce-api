package com.rikkachiu.ecommerce_api.model.pojo;

import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int productId;
    private String productName;
    private ProductCategory category;
    private String imageUrl;
    private Integer price;
    private Integer stock;
    private String description;
    private Date createdDate;
    private Date lastModifiedDate;
}
