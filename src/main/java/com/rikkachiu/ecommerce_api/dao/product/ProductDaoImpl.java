package com.rikkachiu.ecommerce_api.dao.product;

import com.rikkachiu.ecommerce_api.mapper.ProductRowMapper;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 依 id 查詢商品
    @Override
    public ProductDTO getProductById(Integer productId) {

        // sql 語法與欄位映射
        String sql = "SELECT product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date " +
                "FROM product " +
                "WHERE product_id = :productId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", productId);

        // 取得映射物件 list，並檢查是否為空
        List<ProductDTO> productDTOList = namedParameterJdbcTemplate.query(sql, params, new ProductRowMapper());
        if (productDTOList.isEmpty()) {
            return null;
        }

        return productDTOList.get(0);
    }
}
