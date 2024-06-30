package com.backend.dorandoran.mypage.domain.response;

public record PsychologicalChangeTrendResponse(
        Integer dayOfMonth,
        Integer score,
        Long counselId
) {
}
