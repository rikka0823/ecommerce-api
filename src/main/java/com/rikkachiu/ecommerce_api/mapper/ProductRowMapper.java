package com.rikkachiu.ecommerce_api.mapper;

import com.rikkachiu.ecommerce_api.constant.ProductCategory;
import com.rikkachiu.ecommerce_api.model.dto.ProductDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<ProductDTO> {

    @Override
    public ProductDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

        //  product 欄位與物件映射
        ProductDTO productDTO = ProductDTO.builder()
                .productId(rs.getInt("product_id"))
                .productName(rs.getString("product_name"))
                .category(ProductCategory.valueOf(rs.getString("category")))
                .imageUrl(rs.getString("image_url"))
                .price(rs.getInt("price"))
                .stock(rs.getInt("stock"))
                .description(rs.getString("description"))
                .createdDate(rs.getTimestamp("created_date"))
                .lastModifiedDate(rs.getTimestamp("last_modified_date"))
                .build();

        return productDTO;
    }
}
