package com.backend.dorandoran.user.service;

import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.LoginResponse;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserToken;
import com.backend.dorandoran.user.domain.request.SmsSendRequest;
import com.backend.dorandoran.user.domain.request.SmsVerificationRequest;
import com.backend.dorandoran.user.repository.SmsVerificationRepository;
import com.backend.dorandoran.user.repository.UserRepository;
import com.backend.dorandoran.user.repository.querydsl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SmsUtil smsUtil;
    private final SmsVerificationRepository smsVerificationRepository;
    private final UserTokenService userTokenService;
    private final UserQueryRepository userQueryRepository;
    private final UserMentalStateRepository userMentalStateRepository;

    @Transactional(readOnly = true)
    public void sendSms(SmsSendRequest request) {
        compareNameAndPhoneNumber(request);

        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        smsUtil.sendSms(request.phoneNumber(), verificationCode);
        smsVerificationRepository.createSmsVerificationCode(request.phoneNumber(), verificationCode);
    }

    private void compareNameAndPhoneNumber(SmsSendRequest request) {
        Optional<User> userOptional = userRepository.findByNameAndPhoneNumber(request.name(), request.phoneNumber());

        if (userOptional.isPresent() && !request.name().equals(userOptional.get().getName())) {
            throw new CommonException(ErrorCode.NOT_FOUND_USERNAME);
        }
    }

    public LoginResponse verifySms(SmsVerificationRequest request) {
        String redisVerificationCode = smsVerificationRepository.getSmsVerificationCode(request.phoneNumber());
        throwAuthExceptions(request, redisVerificationCode);
        smsVerificationRepository.removeSmsVerificationCode(request.phoneNumber());

        String accessToken = joinOrLogin(request);
        Long userId = Long.parseLong(jwtUtil.getAuthenticationByAccessToken(accessToken).getName());
        return new LoginResponse(accessToken, userMentalStateRepository.existsByUserId(userId));
    }

    private void throwAuthExceptions(SmsVerificationRequest request, String redisVerificationCode) {
        if (!smsVerificationRepository.hasKey(request.phoneNumber()) || !StringUtils.hasText(redisVerificationCode)) {
            throw new CommonException(ErrorCode.EXPIRED_AUTH_CODE);
        } else if (!redisVerificationCode.equals(request.verificationCode())) {
            throw new CommonException(ErrorCode.WRONG_AUTH_CODE);
        }
    }

    @Transactional
    public String joinOrLogin(SmsVerificationRequest request) {
        Optional<UserToken> userOptional = userQueryRepository.findUserTokenByPhoneNumber(request.phoneNumber());
        return userOptional.isPresent() ? updateUserToken(userOptional.get()) : saveUserToken(request);
    }

    private String updateUserToken(UserToken userToken) {
        Authentication authentication = jwtUtil.getAuthenticationByUserId(userToken.getUserId());

        String refreshToken = jwtUtil.createRefreshToken(authentication);
        userToken.updateRefreshToken(refreshToken);
        userTokenService.save(userToken); // TODO 더티체킹으로 처리하는 방법 찾기(쿼리 줄이기)
        return jwtUtil.createAccessToken(authentication);
    }

    private String saveUserToken(SmsVerificationRequest request) {
        User user = User.toUserEntity(request);
        userRepository.save(user);

        Authentication authentication = jwtUtil.getAuthenticationByUserId(user.getId());

        String refreshToken = jwtUtil.createRefreshToken(authentication);
        UserToken userToken = UserToken.toUserTokenEntity(user.getId(), refreshToken);
        userTokenService.save(userToken);
        return jwtUtil.createAccessToken(authentication);
    }

    @Transactional
    public void logout() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        userTokenService.deleteByUserId(userId);
    }

    @Transactional
    public void signOut() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        userTokenService.deleteByUserId(userId);
        userRepository.deleteById(userId);
        // TODO 사용자 관련 심리검사, 상담 등 삭제
    }
}