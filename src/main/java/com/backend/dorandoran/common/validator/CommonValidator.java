package com.backend.dorandoran.common.validator;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor
public class CommonValidator {

    public static void isTrueOrThrow(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new CommonException(errorCode);
        }
    }

    public static void isValidOrThrow(Number number, ErrorCode errorCode) {
        if (number == null || number.doubleValue() <= 0) {
            throw new CommonException(errorCode);
        }
    }

    public static void isNullOrThrow(@Nullable Object object, ErrorCode errorCode) {
        if (object != null) {
            throw new CommonException(errorCode);
        }
    }

    public static void notNullOrThrow(@Nullable Object object, ErrorCode errorCode) {
        if (object == null) {
            throw new CommonException(errorCode);
        }
    }

    public static void notPresentOrThrow(Optional<?> optional, ErrorCode errorCode) {
        if (optional.isEmpty()) {
            throw new CommonException(errorCode);
        }
    }

    public static void hasTextOrThrow(@Nullable String text, ErrorCode errorCode) {
        if (!StringUtils.hasText(text)) {
            throw new CommonException(errorCode);
        }
    }

    public static void notEmptyList(List<?> list, ErrorCode errorCode) {
        if (list.isEmpty()) {
            throw new CommonException(errorCode);
        }
    }

    public static void hasNoEmptyElementOrThrow(List<?> list, ErrorCode errorCode) {
        list.forEach(e -> notNullOrThrow(e, errorCode));
    }
}
