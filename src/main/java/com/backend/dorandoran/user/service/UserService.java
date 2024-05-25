package com.backend.dorandoran.user.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
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

    public String verifySms(SmsVerificationRequest request) {
        String redisVerificationCode = smsVerificationRepository.getSmsVerificationCode(request.phoneNumber());
        if (!smsVerificationRepository.hasKey(request.phoneNumber()) || !StringUtils.hasText(redisVerificationCode)) {
            throw new CommonException(ErrorCode.EXPIRED_AUTH_CODE);
        } else if (!redisVerificationCode.equals(request.verificationCode())) {
            throw new CommonException(ErrorCode.WRONG_AUTH_CODE);
        }
        smsVerificationRepository.removeSmsVerificationCode(request.phoneNumber());

        return joinOrLogin(request);
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
}