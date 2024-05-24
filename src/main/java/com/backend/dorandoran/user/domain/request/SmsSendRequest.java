package com.backend.dorandoran.user.domain.request;

import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.NotNull;

public record SmsSendRequest(
        @NotNull
        @Pattern(regexp = "^[0-9]{11}$", message = "휴대폰번호 11자리를 입력해주세요.")
        String phoneNumber
) {
}