package com.rikkachiu.ecommerce_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private OAuth2OidcService oAuth2OidcService;

    @Autowired
    private OAuth2Service oAuth2Service;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                // CORS 設定
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 設定
                .csrf(csrf -> csrf.disable())

                // OAuth2.0 設定
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(infoEndpoint -> infoEndpoint
                                .oidcUserService(oAuth2OidcService)
                                .userService(oAuth2Service)
                        )
                )

                // JWT 設定
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )

                // 請求路徑設定
                .authorizeHttpRequests(request -> request
                        // 允許所有人註冊、登入、查詢商品、獲取 token、swagger api
                        .requestMatchers(HttpMethod.POST, "/users/register", "/keycloak/getToken", "/keycloak/exchangeAccessToken").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/{productId}", "/keycloak/buildAuthUrl", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 會員、訂單、商品增刪查改權限
                        .requestMatchers(HttpMethod.POST, "/users/{userId}/orders", "/users/login").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
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

    // CORS 設定
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

    // JWT 認證轉換
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        // 封裝 JWT 轉換方法
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // 將 JWT 內 roles 取出
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            if (realmAccess != null) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                if (roles != null) {
                    for (String role : roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }
            return authorities;
        });

        return jwtAuthenticationConverter;
    }
}
