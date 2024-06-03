package com.backend.dorandoran.user.domain;

public record LoginResponse (
        String accessToken,
        Boolean hasPsychologicalAssessmentResult
) {
}