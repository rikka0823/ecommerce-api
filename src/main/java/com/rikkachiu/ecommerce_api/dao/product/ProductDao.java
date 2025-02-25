package com.rikkachiu.ecommerce_api.dao.product;

import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Product;

import java.util.List;

public interface ProductDao {

    // 查詢所有商品
    List<Product> getProducts();

    // 依 id 查詢商品
    Product getProductById(Integer productId);

    // 新增商品
    Integer createProduct(ProductDTO productDTO);

    // 依 id 更新商品
    void updateProduct(Integer id, ProductDTO productDTO);

    // 依 id 刪除商品
    void deleteProductById(Integer productId);
}
