package com.backend.dorandoran.mypage.domain.request;

import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentCategory;
import jakarta.validation.constraints.Digits;
import org.jetbrains.annotations.NotNull;

public record PsychologicalChangeTrendRequest(
        @NotNull
        PsychologicalAssessmentCategory category,
        @NotNull
        @Digits(integer = 2, fraction = 0, message = "숫자만 입력 가능합니다.")
        Integer month
) {
}
