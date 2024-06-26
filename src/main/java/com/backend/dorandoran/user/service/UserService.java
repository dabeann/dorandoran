package com.backend.dorandoran.user.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserToken;
import com.backend.dorandoran.user.domain.request.SmsSendRequest;
import com.backend.dorandoran.user.domain.request.SmsVerificationRequest;
import com.backend.dorandoran.user.domain.request.UserJoinRequest;
import com.backend.dorandoran.user.repository.SmsVerificationRepository;
import com.backend.dorandoran.user.repository.UserRepository;
import com.backend.dorandoran.user.repository.UserTokenRepository;
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
    private final UserTokenRepository userTokenRepository;

    @Transactional(readOnly = true)
    public void sendSms(SmsSendRequest request) {
        compareNameAndPhoneNumber(request);

        String verificationCode = generateVerificationCode();
        smsUtil.sendSms(request.phoneNumber(), verificationCode);
        smsVerificationRepository.createSmsVerificationCode(request.phoneNumber(), verificationCode);
    }

    private void compareNameAndPhoneNumber(SmsSendRequest request) {
        Optional<User> userOptional = userRepository.findByNameAndPhoneNumber(request.name(), request.phoneNumber());
        if (userOptional.isPresent() && !request.name().equals(userOptional.get().getName())) {
            throw new CommonException(ErrorCode.NOT_FOUND_USERNAME);
        }
    }

    private static String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @Transactional
    public String verifySms(SmsVerificationRequest request) {
        String phoneNumber = request.phoneNumber();
        String redisVerificationCode = smsVerificationRepository.getSmsVerificationCode(phoneNumber);
        validateVerificationCode(request, redisVerificationCode);
        smsVerificationRepository.removeSmsVerificationCode(phoneNumber);

        return userRepository.findByPhoneNumber(phoneNumber)
                .map(this::updateUserToken)
                .orElse(null);
    }

    private void validateVerificationCode(SmsVerificationRequest request, String redisVerificationCode) {
        String phoneNumber = request.phoneNumber();
        String verificationCode = request.verificationCode();

        if (!smsVerificationRepository.hasKey(phoneNumber) || !StringUtils.hasText(redisVerificationCode)) {
            throw new CommonException(ErrorCode.EXPIRED_AUTH_CODE);
        } else if (!redisVerificationCode.equals(verificationCode)) {
            throw new CommonException(ErrorCode.WRONG_AUTH_CODE);
        }
    }

    private String updateUserToken(User user) {
        Authentication authentication = jwtUtil.getAuthenticationByUserId(user.getId());
        String refreshToken = jwtUtil.createRefreshToken(authentication);

        Optional<UserToken> optionalUserToken = userTokenRepository.findByUserId(user.getId());
        if (optionalUserToken.isPresent()) {
            optionalUserToken.get().updateRefreshToken(refreshToken);
        } else {
            saveUserToken(user, refreshToken);
        }
        return jwtUtil.createAccessToken(authentication);
    }

    private void saveUserToken(User user, String refreshToken) {
        UserToken userToken = UserToken.toUserTokenEntity(user.getId(), refreshToken);
        userTokenRepository.save(userToken);
    }

    @Transactional
    public String join(UserJoinRequest request) {
        if (userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new CommonException(ErrorCode.ALREADY_EXISTING_USER);
        }

        User user = User.toUserEntity(request);
        userRepository.save(user);

        Authentication authentication = jwtUtil.getAuthenticationByUserId(user.getId());
        String refreshToken = jwtUtil.createRefreshToken(authentication);
        saveUserToken(user, refreshToken);
        return jwtUtil.createAccessToken(authentication);
    }
}