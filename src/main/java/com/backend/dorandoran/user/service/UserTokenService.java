package com.backend.dorandoran.user.service;

import com.backend.dorandoran.user.domain.entity.UserToken;
import com.backend.dorandoran.user.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    public UserToken findByUserId(Long userId) {
        return userTokenRepository.findByUserId(userId);
    }

    public void save(UserToken userToken) {
        userTokenRepository.save(userToken);
    }
}