package com.backend.dorandoran.security.jwt.filter;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.response.ErrorResponse;
import com.backend.dorandoran.common.exception.CommonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFailureFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CommonException te) {
            sendErrorResponse(te.getErrorCode(), response);
        }
    }

    private void sendErrorResponse(ErrorCode errorCode, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorCode.getErrorMessage());
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
