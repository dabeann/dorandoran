package com.backend.dorandoran.sms.service;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:application-cool-sms.yml")
@Component
public class SmsUtil {

    @Value("${API_KEY}")
    private String API_KEY;

    @Value("${API_SECRET}")
    private String API_SECRET_KEY;

    @Value("${FROM_PHONE_NUMBER}")
    private String FROM_PHONE_NUMBER;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendSms(String phoneNumber, String verificationCode) {
        Message message = new Message();
        message.setFrom(FROM_PHONE_NUMBER);
        message.setTo(phoneNumber);
        message.setText("[도란도란] 인증번호[" + verificationCode + "] 타인에게 절대 알려주지 마세요.");

        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
