package com.backend.dorandoran.user.repository.querydsl;

public interface UserQueryRepository {

    void deleteCounselAndDialogByUserId(Long userId);
}