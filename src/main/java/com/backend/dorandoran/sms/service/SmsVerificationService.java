package com.backend.dorandoran.sms.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.sms.repository.SmsVerificationRepository;
import com.backend.dorandoran.sms.request.SmsVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SmsVerificationService {

    private final SmsUtil smsUtil;
    private final SmsVerificationRepository smsVerificationRepository;

    public void sendSms(String phoneNumber) {
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        smsUtil.sendSms(phoneNumber, verificationCode);
        smsVerificationRepository.createSmsVerificationCode(phoneNumber, verificationCode);
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
