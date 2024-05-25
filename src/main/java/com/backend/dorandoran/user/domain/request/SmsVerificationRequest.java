package com.backend.dorandoran.user.domain.request;

import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.NotNull;

public record SmsVerificationRequest(
        @jakarta.validation.constraints.NotNull
        @Pattern(regexp = "^[가-힣]*$", message = "이름은 한글만 가능합니다.")
        String name,
        @NotNull
        @Pattern(regexp = "^[0-9]{11}$", message = "휴대폰번호 11자리를 입력해주세요.")
        String phoneNumber,
        @NotNull
        @Pattern(regexp = "^[0-9]{6}$", message = "인증번호 6자리를 입력해주세요.")
        String verificationCode
) {
}