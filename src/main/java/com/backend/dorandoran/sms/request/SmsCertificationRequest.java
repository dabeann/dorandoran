package com.backend.dorandoran.sms.request;

import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.NotNull;

public record SmsCertificationRequest(
        @NotNull
        @Pattern(regexp = "^\\d{11}$", message = "휴대폰번호는 11자리만 입력 가능합니다.")
        String phoneNumber,
        @NotNull
        @Pattern(regexp = "^\\d{6}$", message = "인증번호는 6자리만 입력 가능합니다.")
        String certificationNumber
) {
}