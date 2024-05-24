package com.backend.dorandoran.sms.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Repository
public class SmsVerificationRepository {

    private final String PREFIX = "sms:";
    private final Duration SMS_LIMIT_TIME = Duration.ofSeconds(5 * 60);

    private final StringRedisTemplate redisTemplate;

    public void createSmsVerificationCode(String phoneNumber, String verificationCode) {
        redisTemplate.opsForValue().set(PREFIX + phoneNumber, verificationCode, SMS_LIMIT_TIME);
    }

    public String getSmsVerificationCode(String phoneNumber) {
        return redisTemplate.opsForValue().get(PREFIX + phoneNumber);
    }

    public void removeSmsVerificationCode(String phoneNumber) {
        redisTemplate.delete(PREFIX + phoneNumber);
    }

    public boolean hasKey(String phoneNumber) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + phoneNumber));
    }
}