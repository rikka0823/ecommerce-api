package com.rikkachiu.ecommerce_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI docOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ecommerce-api")
                        .description("實作電商系統常見功能，如會員、訂單、商品")
                );
    }
}
