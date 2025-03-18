package com.rikkachiu.ecommerce_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        // 設定 cors
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("POST", "GET", "UPDATE", "DELETE"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        // 設置允許路徑
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // session 設定
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

                // CORS 設定
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 設定
                .csrf(csrf -> csrf.disable())

                // form 表單、httpBasic 登入設定
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())

                // 登入紀錄過濾
                .addFilterAfter(new SecurityFilter(), BasicAuthenticationFilter.class)

                // 請求路徑設定
                .authorizeHttpRequests(request -> request
                        // 允許所有人註冊、登入、查詢商品
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/{productId}").permitAll()

                        // 會員、訂單、商品增刪查改權限
                        .requestMatchers(HttpMethod.POST,  "/users/{userId}/orders", "/users/login").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers(HttpMethod.GET, "/users/{userId}/orders").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/products/{productId}").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/products/{productId}").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/users/{userId}/delete", "/users/{userId}/orders/{orderId}").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")

                        // 管理者權限
                        .requestMatchers(HttpMethod.GET, "/users/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/update").hasRole("ADMIN")

                        // 例外限制
                        .anyRequest().denyAll()
                )


                .build();
    }
}
