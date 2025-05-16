package com.rikkachiu.ecommerce_api.service.product;

import com.rikkachiu.ecommerce_api.model.dto.ProductDto;
import com.rikkachiu.ecommerce_api.model.dto.ProductQueryParamsDto;
import com.rikkachiu.ecommerce_api.model.pojo.Product;

import java.util.List;

public interface ProductService {

    // 查詢商品總數
    Integer getProductCount(ProductQueryParamsDto productQueryParamsDto);

    // 查詢所有商品，依不同條件
    List<Product> getProducts(ProductQueryParamsDto productQueryParamsDto);

    // 依 id 查詢商品
    Product getProductById(Integer productId);

    // 新增商品
    Integer createProduct(ProductDto productDto);

    // 依 id 更新商品
    void updateProduct(Integer id, ProductDto productDto);

    // 依 id 刪除商品
    void deleteProductById(Integer productId);
}
