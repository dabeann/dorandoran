package com.backend.dorandoran.mypage.repository;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;

public interface MypageQueryRepository {

    PsychologicalAssessmentResponse getUserFirstAssessmentResult(Long userId);
}
