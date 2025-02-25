package com.rikkachiu.ecommerce_api.service.product;

import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    // 依 id 查詢商品
    @Override
    public Product getProductById(Integer productId) {
        return productDao.getProductById(productId);
    }

    // 新增商品
    @Override
    public Integer createProduct(ProductDTO productDTO) {
        return productDao.createProduct(productDTO);
    }

    // 依 id 更新商品
    @Override
    public void updateProduct(Integer id, ProductDTO productDTO) {
        productDao.updateProduct(id, productDTO);
    }

    // 依 id 刪除商品
    @Override
    public void deleteProductById(Integer productId) {
        productDao.deleteProductById(productId);
    }
}
