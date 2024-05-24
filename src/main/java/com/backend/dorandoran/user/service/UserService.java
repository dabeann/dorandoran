package com.backend.dorandoran.user.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
import com.backend.dorandoran.user.domain.request.SmsSendRequest;
import com.backend.dorandoran.user.repository.SmsVerificationRepository;
import com.backend.dorandoran.user.domain.request.SmsVerificationRequest;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.request.LoginRequest;
import com.backend.dorandoran.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SmsUtil smsUtil;
    private final SmsVerificationRepository smsVerificationRepository;

    @Transactional
    public String joinOrLogin(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByNameAndPhoneNumber(request.name(), request.phoneNumber());
        User user;
        if (userOptional.isPresent()) { // 로그인
            user = userOptional.get();
            if (!request.name().equals(user.getName())) {
                throw new CommonException(ErrorCode.NOT_FOUND_USERNAME);
            }
        } else { // 회원가입
            user = User.toUserEntity(request);
            userRepository.save(user);
        }
        return jwtUtil.saveToken(user.getId());
    }

    public void sendSms(SmsSendRequest request) {
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        smsUtil.sendSms(request.phoneNumber(), verificationCode);
        smsVerificationRepository.createSmsVerificationCode(request.phoneNumber(), verificationCode);
    }

    public void verifySms(SmsVerificationRequest request) {
        String redisVerificationCode = smsVerificationRepository.getSmsVerificationCode(request.phoneNumber());
        if (smsVerificationRepository.hasKey(request.phoneNumber())) {
            throw new CommonException(ErrorCode.EXPIRED_AUTH_CODE);
        } else if (!redisVerificationCode.equals(request.verificationCode())) {
            throw new CommonException(ErrorCode.WRONG_AUTH_CODE);
        }
        smsVerificationRepository.removeSmsVerificationCode(request.phoneNumber());
    }
}