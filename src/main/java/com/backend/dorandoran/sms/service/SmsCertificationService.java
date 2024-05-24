package com.backend.dorandoran.sms.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.sms.repository.SmsCertificationRepository;
import com.backend.dorandoran.sms.request.SmsCertificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsCertificationService {

    private final SmsUtil smsUtil;
    private final SmsCertificationRepository smsCertificationRepository;

    public void sendSms(String phoneNumber) {
        int randomNumber = (int) (Math.random() * 9000) + 1000;
        String certificationNumber = String.valueOf(randomNumber);
        smsUtil.sendSms(phoneNumber, certificationNumber);
        smsCertificationRepository.createSmsCertification(phoneNumber, certificationNumber);
    }

    public void verifySms(SmsCertificationRequest request) {
        if (isVerify(request)) {
            throw new CommonException(ErrorCode.WRONG_AUTH_CODE);
        }
        smsCertificationRepository.removeSmsCertification(request.phoneNumber());
    }

    private boolean isVerify(SmsCertificationRequest request) {
        return !(smsCertificationRepository.hasKey(request.phoneNumber()) &&
                smsCertificationRepository.getSmsCertification(request.phoneNumber())
                        .equals(request.certificationNumber()));
    }
}
