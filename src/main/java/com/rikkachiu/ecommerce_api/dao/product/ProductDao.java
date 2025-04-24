package com.rikkachiu.ecommerce_api.dao.product;

import com.rikkachiu.ecommerce_api.model.dto.ProductDto;
import com.rikkachiu.ecommerce_api.model.dto.ProductQueryParamsDto;
import com.rikkachiu.ecommerce_api.model.pojo.Product;

import java.util.List;

public interface ProductDao {

    // 查詢商品總數
    Integer getProductCount(ProductQueryParamsDto productQueryParamsDTO);

    // 查詢所有商品，依不同條件
    List<Product> getProducts(ProductQueryParamsDto productQueryParamsDTO);

    // 依 id 查詢商品
    Product getProductById(Integer productId);

    // 新增商品
    Integer createProduct(ProductDto productDTO);

    // 依 id 更新商品
    void updateProduct(Integer id, ProductDto productDTO);

    // 批量更新商品庫存
    void updateProductStock(List<Product> productList);

    // 依 id 刪除商品
    void deleteProductById(Integer productId);
}
