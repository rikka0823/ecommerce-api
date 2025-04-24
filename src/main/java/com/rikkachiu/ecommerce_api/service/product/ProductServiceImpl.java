package com.rikkachiu.ecommerce_api.service.product;

import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.model.dto.ProductDto;
import com.rikkachiu.ecommerce_api.model.dto.ProductQueryParamsDto;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    // 查詢商品總數
    @Cacheable(cacheNames = "ecommerce_getProductCount",
            key = "#p0.category + '-' + #p0.search + '-' + #p0.orderBy + '-' + " +
                    "#p0.sort + '-' + " + "#p0.limit + '-' + #p0.offset",
            unless = "#result == null")
    @Override
    public Integer getProductCount(ProductQueryParamsDto productQueryParamsDTO) {
        return productDao.getProductCount(productQueryParamsDTO);
    }

    // 查詢所有商品，依不同條件
    @Cacheable(cacheNames = "ecommerce_getProducts",
            key = "#p0.category + '-' + #p0.search + '-' + #p0.orderBy + '-' + " +
                    "#p0.sort + '-' + " + "#p0.limit + '-' + #p0.offset",
            unless = "#result == null")
    @Override
    public List<Product> getProducts(ProductQueryParamsDto productQueryParamsDTO) {
        return productDao.getProducts(productQueryParamsDTO);
    }

    // 依 id 查詢商品
    @Cacheable(cacheNames = "ecommerce_getProductById", key = "'productId-' + #p0",
            unless = "#result == null")
    @Override
    public Product getProductById(Integer productId) {
        return productDao.getProductById(productId);
    }

    // 新增商品
    @CacheEvict(cacheNames = {"ecommerce_getProductCount", "ecommerce_product", "ecommerce_getProductById"},
            allEntries = true)
    @Override
    public Integer createProduct(ProductDto productDTO) {
        return productDao.createProduct(productDTO);
    }

    // 依 id 更新商品
    @CacheEvict(cacheNames = {"ecommerce_getProductCount", "ecommerce_product", "ecommerce_getProductById"},
            allEntries = true)
    @Override
    public void updateProduct(Integer id, ProductDto productDTO) {
        productDao.updateProduct(id, productDTO);
    }

    // 依 id 刪除商品
    @CacheEvict(cacheNames = {"ecommerce_getProductCount", "ecommerce_product", "ecommerce_getProductById"},
            allEntries = true)
    @Override
    public void deleteProductById(Integer productId) {
        productDao.deleteProductById(productId);
    }
}
