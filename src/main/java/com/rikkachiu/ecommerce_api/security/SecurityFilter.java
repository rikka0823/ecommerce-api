package com.rikkachiu.ecommerce_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 記錄登入帳號、位置、時間
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userAgent = request.getHeader("User-Agent");
        if (request.getRequestURI().equals("/users/login")) {
            logger.info("使用者： {} 正從 {} 嘗試登入，{}", authentication.getName(), userAgent, new Date());
        }

        filterChain.doFilter(request, response);
    }
}
