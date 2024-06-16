package com.backend.dorandoran.mypage.domain.request;

import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.NotNull;

public record CompletedCounselRequest(
        @NotNull
        @Pattern(regexp = "\\d{4}\\d{2}\\d{2}", message = "날짜는 YYYYMMDD 형식이어야 합니다.")
        String counselDate
) {
}
