package com.rikkachiu.ecommerce_api.service.product;

import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;

public interface ProductService {

    // 依 id 查詢商品
    ProductDTO getProductById(Integer productId);

}
