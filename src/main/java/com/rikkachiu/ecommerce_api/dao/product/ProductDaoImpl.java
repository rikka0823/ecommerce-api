package com.rikkachiu.ecommerce_api.dao.product;

import com.rikkachiu.ecommerce_api.mapper.ProductRowMapper;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 依 id 查詢商品
    @Override
    public Product getProductById(Integer productId) {

        // sql 語法與欄位映射
        String sql = "SELECT product_id, product_name, category, image_url, " +
                "price, stock, description, created_date, last_modified_date " +
                "FROM product " +
                "WHERE product_id = :productId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", productId);

        // 取得映射物件 list，並檢查是否為空
        List<Product> productList = namedParameterJdbcTemplate
                .query(sql, params, new ProductRowMapper());
        if (productList.isEmpty()) {
            return null;
        }

        return productList.get(0);
    }

    // 新增商品
    @Override
    public Integer createProduct(ProductDTO productDTO) {

        // sql 語法與欄位映射
        String sql = "INSERT INTO product (product_name, category, image_url, price, stock, " +
                "description, created_date, last_modified_date) " +
                "VALUES (:productName, :category, :imageUrl, :price, :stock, " +
                ":description, :createdDate, :lastModifiedDate)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productName", productDTO.getProductName())
                .addValue("category", productDTO.getCategory().name())
                .addValue("imageUrl", productDTO.getImageUrl())
                .addValue("price", productDTO.getPrice())
                .addValue("stock", productDTO.getStock())
                .addValue("description", productDTO.getDescription())
                .addValue("createdDate", new Date())
                .addValue("lastModifiedDate", new Date());

        // 新增商品，並取得商品 id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
        int id = keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;

        return id;
    }
}
