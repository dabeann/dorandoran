package com.backend.dorandoran.user.service;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
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
        messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.coolsms.co.kr");
    }

    public void sendSms(String phoneNumber, String verificationCode) {
        Message message = new Message();
        message.setFrom(FROM_PHONE_NUMBER);
        message.setTo(phoneNumber);
        message.setText("[도란도란] 인증번호[" + verificationCode + "] 타인에게 절대 알려주지 마세요.");

        messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendEmergencySms(String phoneNumber, String name, String userPhoneNumber) {
        Message message = new Message();
        message.setFrom(FROM_PHONE_NUMBER);
        message.setTo(phoneNumber);
        message.setText(name + "님이 현재 심리적으로 위급한 상황입니다. " + name + "님의 전화번호 : " + userPhoneNumber);

        messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}