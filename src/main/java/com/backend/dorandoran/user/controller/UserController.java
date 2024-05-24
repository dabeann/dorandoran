package com.backend.dorandoran.user.controller;

import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
import com.backend.dorandoran.sms.request.SmsCertificationRequest;
import com.backend.dorandoran.sms.service.SmsCertificationService;
import com.backend.dorandoran.user.domain.request.LoginRequest;
import com.backend.dorandoran.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API입니다.")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SmsCertificationService smsCertificationService;

    @Operation(summary = "summary : 회원가입/로그인",
            description = """
                    ## 요청 :
                    - String name (한글만 가능) (필수)
                    - String phoneNumber (숫자 11자리만 가능) (필수)
                    ## 응답 :
                    - header(Authorization Bearer 토큰)
                    - 심리검사 진행 여부(boolean) (추가 예정)
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/login")
    ResponseEntity<CommonResponse<Boolean>> login(@Valid @RequestBody LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        headers.add(HttpHeaders.AUTHORIZATION, !authentication.isAuthenticated() ?
                userService.joinOrLogin(request) : jwtUtil.createAccessToken(authentication));
        // TODO 심리검사 여부 추가
        return new ResponseEntity<>(new CommonResponse<>("회원가입/로그인", null), headers, HttpStatus.OK);
    }

    @PostMapping("/send-sms")
    ResponseEntity<CommonResponse<String>> sendSms(@RequestParam("phoneNumber") String phoneNumber) {
        smsCertificationService.sendSms(phoneNumber);
        return new ResponseEntity<>(new CommonResponse<>("SMS 전송", "success"), HttpStatus.OK);
    }

    @PostMapping("/confirm-sms")
    ResponseEntity<CommonResponse<Boolean>> smsVerification(@RequestBody SmsCertificationRequest request) {
        smsCertificationService.verifySms(request);
        return new ResponseEntity<>(new CommonResponse<>("SMS 인증번호 확인", true), HttpStatus.OK);
    }
}