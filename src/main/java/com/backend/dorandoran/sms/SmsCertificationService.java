package com.backend.dorandoran.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsCertificationService {

    private final SmsUtil smsUtil;
    private final SmsCertificationRepository smsCertificationRepository;

    public void sendSms(SmsCertificationRequest request) {
        String to = request.getPhone();
        int randomNumber = (int) (Math.random() * 9000) + 1000;
        String certificationNumber = String.valueOf(randomNumber);
        smsUtil.sendSms(to, certificationNumber);
        smsCertificationRepository.createSmsCertification(to, certificationNumber);
    }

    public void verifySms(SmsCertificationRequest request) throws Exception {
        if (isVerify(request)) {
            throw new Exception("인증번호가 일치하지 않습니다.");
        }
        smsCertificationRepository.removeSmsCertification(request.getPhone());
    }

    private boolean isVerify(SmsCertificationRequest request) {
        return !(smsCertificationRepository.hasKey(request.getPhone()) &&
                smsCertificationRepository.getSmsCertification(request.getPhone())
                        .equals(request.getCertificationNumber()));
    }
}
