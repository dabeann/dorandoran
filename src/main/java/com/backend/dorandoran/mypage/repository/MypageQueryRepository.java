package com.backend.dorandoran.mypage.repository;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.mypage.domain.request.PsychologicalChangeTrendRequest;
import com.backend.dorandoran.mypage.domain.response.CompletedCounselResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.domain.response.PsychologicalChangeTrendResponse;

import java.util.List;

public interface MypageQueryRepository {

    PsychologicalAssessmentResponse getUserFirstAssessmentResult(Long userId);

    MypageMainResponse getUserInfoForMain(Long userId);

    List<PsychologicalChangeTrendResponse> getUserPsychologicalChangeTrend(Long userId, PsychologicalChangeTrendRequest request);

    CompletedCounselResponse getCompletedCounsel(Long counselId);
}
