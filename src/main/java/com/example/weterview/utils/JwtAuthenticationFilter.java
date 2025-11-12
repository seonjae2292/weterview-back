package com.example.weterview.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtUtil.resolveToken(request);
        if (token != null && jwtUtil.isTokenExpired(token)) {
            Authentication auth = jwtUtil.createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // **반드시** 다음 필터로 요청을 넘겨줘야 함
        filterChain.doFilter(request, response);
    }
}
