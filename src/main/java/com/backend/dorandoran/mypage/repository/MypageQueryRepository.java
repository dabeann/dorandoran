package com.backend.dorandoran.mypage.repository;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;

public interface MypageQueryRepository {

    PsychologicalAssessmentResponse getUserFirstAssessmentResult(Long userId);

    MypageMainResponse getUserInfoForMain(Long userId);
}
