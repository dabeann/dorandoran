package com.backend.dorandoran.mypage.service;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.repository.MypageQueryRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;
    private final MypageQueryRepository mypageQueryRepository;

    public MypageMainResponse getUserInfoForMypageMain() {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return new MypageMainResponse(userId, user.getName());
    }

    public PsychologicalAssessmentResponse getUserFirstAssessmentResult() {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        return mypageQueryRepository.getUserFirstAssessmentResult(userId);
    }
}