package com.rikkachiu.ecommerce_api.dao.product;

import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;

public interface ProductDao {

    // 依 id 查詢商品
    ProductDTO getProductById(Integer productId);
}
