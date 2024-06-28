package com.backend.dorandoran.mypage.service;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.mypage.domain.request.PsychologicalChangeTrendRequest;
import com.backend.dorandoran.mypage.domain.response.CompletedCounselResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.domain.response.PsychologicalChangeTrendResponse;
import com.backend.dorandoran.mypage.repository.MypageQueryRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.repository.UserRepository;
import com.backend.dorandoran.user.repository.UserTokenRepository;
import com.backend.dorandoran.user.repository.querydsl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MypageService {

    private final MypageQueryRepository mypageQueryRepository;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserMentalStateRepository userMentalStateRepository;
    private final UserQueryRepository userQueryRepository;

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

    public CompletedCounselResponse getCompletedCounsel(Long counselId) {
        return mypageQueryRepository.getCompletedCounsel(counselId);
    }

    @Transactional
    public void logout() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        userTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void signOut() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        userTokenRepository.deleteByUserId(userId);
        userMentalStateRepository.deleteByUserId(userId);
        userQueryRepository.deleteCounselAndDialogByUserId(userId);
        userRepository.deleteById(userId);
    }
}