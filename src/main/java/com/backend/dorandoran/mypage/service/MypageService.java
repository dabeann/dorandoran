package com.backend.dorandoran.mypage.service;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.mypage.domain.request.CompletedCounselRequest;
import com.backend.dorandoran.mypage.domain.request.PsychologicalChangeTrendRequest;
import com.backend.dorandoran.mypage.domain.response.CompletedCounselResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.domain.response.PsychologicalChangeTrendResponse;
import com.backend.dorandoran.mypage.repository.MypageQueryRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MypageService {

    private final MypageQueryRepository mypageQueryRepository;

    public MypageMainResponse getUserInfoForMypageMain() {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        return mypageQueryRepository.getUserInfoForMain(userId);
    }

    public PsychologicalAssessmentResponse getUserFirstAssessmentResult() {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        return mypageQueryRepository.getUserFirstAssessmentResult(userId);
    }

    public List<PsychologicalChangeTrendResponse> getUserPsychologicalChangeTrend(PsychologicalChangeTrendRequest request) {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        return mypageQueryRepository.getUserPsychologicalChangeTrend(userId, request);
    }

    public List<CompletedCounselResponse> getCompletedCounselList(CompletedCounselRequest request) {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        return mypageQueryRepository.getCompletedCounselList(userId, request.counselDate());
    }
}