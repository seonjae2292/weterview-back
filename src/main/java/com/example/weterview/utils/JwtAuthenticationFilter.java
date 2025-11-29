package com.example.weterview.utils;

import com.example.weterview.config.CustomUserDetails;
import com.example.weterview.entity.User;
import com.example.weterview.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtUtil.resolveToken(request);

        if (token != null && jwtUtil.isTokenExpired(token)) {
            String kakaoUserNumber = jwtUtil.getKakaoUserNumFromToken(token);

            User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

//            Authentication auth = jwtUtil.createAuthentication(token);

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // **반드시** 다음 필터로 요청을 넘겨줘야 함
        filterChain.doFilter(request, response);
    }
}
