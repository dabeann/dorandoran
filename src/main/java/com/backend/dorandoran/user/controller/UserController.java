package com.backend.dorandoran.user.controller;

import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
import com.backend.dorandoran.user.domain.request.LoginRequest;
import com.backend.dorandoran.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    ResponseEntity<CommonResponse<Boolean>> login(@Valid @RequestBody LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        headers.add(HttpHeaders.AUTHORIZATION, !authentication.isAuthenticated() ?
                userService.joinOrLogin(request) : jwtUtil.createAccessToken(authentication));
        return new ResponseEntity<>(new CommonResponse<>("회원가입/로그인", null), headers, HttpStatus.OK);
    }
}