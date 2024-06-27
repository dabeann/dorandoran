package com.backend.dorandoran.user.domain.request;

import com.backend.dorandoran.common.domain.user.UserAgency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserJoinRequest(
        @NotNull
        @Pattern(regexp = "^[가-힣]*$", message = "이름은 한글만 가능합니다.")
        String name,
        @NotNull
        @Pattern(regexp = "^[0-9]{11}$", message = "휴대폰번호 11자리를 입력해주세요.")
        String phoneNumber,
        @NotNull
        UserAgency userAgency
) {
}
