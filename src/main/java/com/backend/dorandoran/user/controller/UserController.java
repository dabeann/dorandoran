package com.backend.dorandoran.user.controller;

import com.backend.dorandoran.assessment.service.PsychologicalAssessmentService;
import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.user.domain.LoginResponse;
import com.backend.dorandoran.user.domain.request.SmsSendRequest;
import com.backend.dorandoran.user.domain.request.SmsVerificationRequest;
import com.backend.dorandoran.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자", description = "사용자 관련 API입니다.")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
class UserController {

    private final UserService userService;
    private final PsychologicalAssessmentService psychologicalAssessmentService;

    @Operation(summary = "summary : SMS 인증번호 전송",
            description = """
                    ## 요청 :
                    - String name (한글만 가능) (필수)
                    - String phoneNumber (숫자 11자리만 가능) (필수)
                    ## 응답 :
                    - 200, "Success"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/send-sms")
    ResponseEntity<CommonResponse<String>> sendSms(@Valid @RequestBody SmsSendRequest smsSendRequest) {
        userService.sendSms(smsSendRequest);
        return new ResponseEntity<>(new CommonResponse<>("SMS 인증번호 전송", "Success"), HttpStatus.OK);
    }

    @Operation(summary = "summary : 회원가입/로그인",
            description = """
                    ## 요청 :
                    - String name (한글만 가능) (필수)
                    - String phoneNumber (숫자 11자리만 가능) (필수)
                    - String verificationCode (숫자 6자리만 가능) (필수)
                    - String userAgency 소속기관 (필수)
                    ## 응답 :
                    - Header(Authorization Bearer 토큰)
                    - Boolean data 심리검사 진행 여부
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/login")
    ResponseEntity<CommonResponse<Boolean>> verifyCodeAndLogin(@Valid @RequestBody SmsVerificationRequest request) {
        LoginResponse loginResponse = userService.verifySms(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, loginResponse.accessToken());

        return new ResponseEntity<>(new CommonResponse<>("회원가입/로그인",
                loginResponse.hasPsychologicalAssessmentResult()), headers, HttpStatus.OK);
    }
}