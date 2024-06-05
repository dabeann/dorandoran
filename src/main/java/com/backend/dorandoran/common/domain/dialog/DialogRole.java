package com.backend.dorandoran.common.domain.dialog;

public enum DialogRole {
    FROM_CONSULTANT("상담원"),
    FROM_USER("내담자");

    private final String koreanName;

    DialogRole(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
