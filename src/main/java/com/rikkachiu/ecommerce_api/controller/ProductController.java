package com.rikkachiu.ecommerce_api.controller;

import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import com.rikkachiu.ecommerce_api.constant.ProductOrderBy;
import com.rikkachiu.ecommerce_api.constant.ProductSort;
import com.rikkachiu.ecommerce_api.model.dto.PageDTO;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import com.rikkachiu.ecommerce_api.model.dto.ProductQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import com.rikkachiu.ecommerce_api.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "商品功能", description = "查詢所有商品，依不同條件、依 id 查詢商品、新增商品、依 id 更新商品、依 id 刪除商品")
@Validated
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "查詢", description = "查詢所有商品，依不同條件")
    @GetMapping("/products")
    public ResponseEntity<PageDTO<Product>> getProducts(
            // filtering
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,

            // sorting
            @RequestParam(required = false) ProductOrderBy orderBy,
            @RequestParam(required = false) ProductSort sort,

            // pagination
            @RequestParam(defaultValue = "5") @Max(25) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {
        // sorting 預設值
        if (orderBy == null) {
            orderBy = ProductOrderBy.LAST_MODIFIED_DATE;
        }
        if (sort == null) {
            sort = ProductSort.DESC;
        }

        // 將查詢參數封裝
        ProductQueryParamsDTO productQueryParamsDTO = ProductQueryParamsDTO.builder()
                .category(category)
                .search(search)
                .orderBy(orderBy.name().toLowerCase())
                .sort(sort.name())
                .limit(limit)
                .offset(offset)
                .build();

        // 將 返回資料封裝在 PageDTO<Product>
        PageDTO<Product> pageDTO =  new PageDTO<>();
        pageDTO.setLimit(limit);
        pageDTO.setOffset(offset);
        pageDTO.setTotal(productService.getProductCount(productQueryParamsDTO));
        pageDTO.setResults(productService.getProducts(productQueryParamsDTO));

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES))
                .body(pageDTO);
    }

    @Operation(summary = "查詢", description = "依 productId 查詢商品")
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        // 依 id 取得物件
        Product product = productService.getProductById(productId);

        // 檢查是否為 null
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES))
                .body(product);
    }

    @Operation(summary = "新增商品")
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

    @Operation(summary = "更新", description = "依 productId 更新商品")
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

    @Operation(summary = "刪除", description = "依 productId 刪除商品")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Integer productId) {
        // 刪除商品
        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
