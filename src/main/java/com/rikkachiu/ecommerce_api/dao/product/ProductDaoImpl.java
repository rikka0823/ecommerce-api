package com.rikkachiu.ecommerce_api.dao.product;

import com.rikkachiu.ecommerce_api.mapper.ProductRowMapper;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import com.rikkachiu.ecommerce_api.model.dto.ProductQueryParamsDTO;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //新增篩選條件 sql
    private void addFilteringSql(ProductQueryParamsDTO productQueryParamsDTO, StringBuilder sql, MapSqlParameterSource params) {
        // 依據 category 添加篩選條件
        if (productQueryParamsDTO.getCategory() != null) {
            sql.append(" AND category = :category");
            params.addValue("category", productQueryParamsDTO.getCategory().name());
        }

        // 依據 search 添加篩選條件
        if (productQueryParamsDTO.getSearch() != null &&
                !productQueryParamsDTO.getSearch().trim().isEmpty()) {
            sql.append(" AND product_name LIKE :search");
            params.addValue("search", "%" + productQueryParamsDTO.getSearch().trim() + "%");
        }
    }

    // 查詢商品總數
    @Override
    public Integer getProductCount(ProductQueryParamsDTO productQueryParamsDTO) {
        // sql 語法與欄位映射
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM product WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        //新增篩選條件 sql
        addFilteringSql(productQueryParamsDTO, sql, params);

        // 取得商品總數
        Integer count = namedParameterJdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        if (count == null) {
            count = 0;
        }

        return count;
    }

    // 查詢所有商品
    @Override
    public List<Product> getProducts(ProductQueryParamsDTO productQueryParamsDTO) {
        // sql 語法與欄位映射
        StringBuilder sql = new StringBuilder("SELECT product_id, product_name, category, image_url, " +
                "price, stock, description, created_date, last_modified_date " +
                "FROM product " +
                "WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        //新增篩選條件 sql
        addFilteringSql(productQueryParamsDTO, sql, params);

        // 依據 orderBy, sort 排序
        sql.append(" ORDER BY ")
                .append(productQueryParamsDTO.getOrderBy())
                .append(" ")
                .append(productQueryParamsDTO.getSort());

        // 依據 limit, offset 分頁
        sql.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", productQueryParamsDTO.getLimit());
        params.addValue("offset", productQueryParamsDTO.getOffset());

        return namedParameterJdbcTemplate.query(sql.toString(), params, new ProductRowMapper());
    }

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

    // 依 id 更新商品
    @Override
    public void updateProduct(Integer productId, ProductDTO productDTO) {
        // sql 語法與欄位映射
        String sql = "UPDATE product " +
                "SET product_name = :productName, category = :category, image_url = :imageUrl, price = :price, " +
                "stock = :stock, description = :description, last_modified_date = :lastModifiedDate " +
                "WHERE product_id = :productId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productName", productDTO.getProductName())
                .addValue("category", productDTO.getCategory().name())
                .addValue("imageUrl", productDTO.getImageUrl())
                .addValue("price", productDTO.getPrice())
                .addValue("stock", productDTO.getStock())
                .addValue("description", productDTO.getDescription())
                .addValue("lastModifiedDate", new Date())
                .addValue("productId", productId);

        // 更新商品
        namedParameterJdbcTemplate.update(sql, params);
    }

    // 依 id 刪除商品
    @Override
    public void deleteProductById(Integer productId) {
        // sql 語法與欄位映射
        String sql = "DELETE FROM product WHERE product_id = :productId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", productId);

        // 刪除商品
        namedParameterJdbcTemplate.update(sql, params);
    }
}
