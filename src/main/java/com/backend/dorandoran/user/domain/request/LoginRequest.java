package com.backend.dorandoran.user.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotNull
        @Pattern(regexp = "^[가-힣]*$", message = "이름은 한글만 가능합니다.")
        String name,
        @NotNull
        @Pattern(regexp = "^\\d{11}$", message = "휴대폰번호는 11자리만 가능합니다.")
        String phoneNumber
) {
}