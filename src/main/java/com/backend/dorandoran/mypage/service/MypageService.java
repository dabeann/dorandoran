package com.backend.dorandoran.mypage.service;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.repository.MypageQueryRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}