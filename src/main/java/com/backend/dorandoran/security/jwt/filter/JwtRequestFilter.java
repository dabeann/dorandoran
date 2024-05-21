package com.backend.dorandoran.security.jwt.filter;

import com.backend.dorandoran.security.jwt.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        log.info("accessToken = {}", accessToken);

        if (StringUtils.hasText(accessToken)) {
            if (jwtUtil.validateToken(accessToken)) {
                Authentication authentication = jwtUtil.getAuthenticationByAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else { // AccessToken이 유효하지 않음 -> 토큰 재할당
                Long userId = Long.valueOf(jwtUtil.getAuthenticationByAccessToken(accessToken).getName()); // 확인 필요
                String reissuedAccessToken = jwtUtil.reissuedAccessToken(userId);
                Authentication authentication = jwtUtil.getAuthenticationByAccessToken(reissuedAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(bearer) && bearer.startsWith("Bearer ") ? bearer.substring(7) : null;
    }
}