package com.backend.dorandoran.security.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserInfoUtil {

    public static Long getUserIdOrDefault() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return !userId.trim().equals("Anonymous") ? Long.parseLong(userId) : 0L;
    }

    public static Long getUserIdOrThrow() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.trim().equals("Anonymous")) {
            throw new CommonException(ErrorCode.EMPTY_TOKEN);
        }
        return Long.parseLong(userId);
    }
}
