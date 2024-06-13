package com.backend.dorandoran.common.domain.dialog;

import lombok.Getter;

@Getter
public enum DialogRole {
    FROM_CONSULTANT("상담원"),
    FROM_USER("내담자");

    private final String koreanName;

    DialogRole(String koreanName) {
        this.koreanName = koreanName;
    }
}
