package com.backend.dorandoran.user.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.security.jwt.service.JwtUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.request.LoginRequest;
import com.backend.dorandoran.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public String joinOrLogin(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByNameAndPhoneNumber(request.name(), request.phoneNumber());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!request.name().equals(user.getName())) {
                throw new CommonException(ErrorCode.NOT_FOUND_USERNAME);
            } else { // 로그인
                return jwtUtil.reissuedAccessToken(user.getId());
            }
        } else { // 회원가입
            // TODO 핸드폰번호 인증 처리
            User user = User.toUserEntity(request);
            userRepository.save(user);
            return jwtUtil.saveToken(user.getId());
        }
    }
}