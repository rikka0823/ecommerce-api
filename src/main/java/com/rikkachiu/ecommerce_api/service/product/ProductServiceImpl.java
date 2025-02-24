package com.rikkachiu.ecommerce_api.service.product;

import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    // 依 id 查詢商品
    @Override
    public ProductDTO getProductById(Integer productId) {
        return productDao.getProductById(productId);
    }
}
