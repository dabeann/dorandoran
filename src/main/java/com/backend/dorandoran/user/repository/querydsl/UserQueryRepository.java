package com.backend.dorandoran.user.repository.querydsl;

import com.backend.dorandoran.user.domain.entity.UserToken;

import java.util.Optional;

public interface UserQueryRepository {

    Optional<UserToken> findUserTokenByPhoneNumber(String phoneNumber);

    void deleteCounselAndDialogByUserId(Long userId);
}