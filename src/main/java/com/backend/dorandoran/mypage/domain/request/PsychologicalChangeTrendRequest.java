package com.backend.dorandoran.mypage.domain.request;

import com.backend.dorandoran.common.domain.PsychologicalAssessmentCategory;
import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.NotNull;

public record PsychologicalChangeTrendRequest(
        @NotNull
        PsychologicalAssessmentCategory category,
        @NotNull
        @Pattern(regexp = "^[0-9]$", message = "요청월은 숫자만 가능합니다.")
        Integer month
) {
}
