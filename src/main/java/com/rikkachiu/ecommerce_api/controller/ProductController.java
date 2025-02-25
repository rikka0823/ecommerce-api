package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import com.rikkachiu.ecommerce_api.service.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    // 查詢所有商品
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
    }

    // 依 id 查詢商品
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        // 依 id 取得物件
        Product product = productService.getProductById(productId);

        // 檢查是否為 null
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    // 新增商品
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        // 取得 id 及對應物件
        int id = productService.createProduct(productDTO);
        Product product = productService.getProductById(id);

        // 檢查是否為 null
        if (product == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // 依 id 更新商品
    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductDTO productDTO) {
        // 檢查商品 id 是否存在
        if (productService.getProductById(productId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 更新商品
        productService.updateProduct(productId, productDTO);

        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(productId));
    }

    // 依 id 刪除商品
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Integer productId) {
        // 刪除商品
        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
