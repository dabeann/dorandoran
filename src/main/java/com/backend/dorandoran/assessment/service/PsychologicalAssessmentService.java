package com.backend.dorandoran.assessment.service;

import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PsychologicalAssessmentService {

    private final UserMentalStateRepository userMentalStateRepository;

    public Boolean hasPsychologicalAssessmentResult() {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        Optional<UserMentalState> userMentalState = userMentalStateRepository.findByUserId(userId);
        return userMentalState.isPresent();
    }
}
