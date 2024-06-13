package com.backend.dorandoran.common.domain.counsel;

import lombok.Getter;

@Getter
public enum SuggestCallCenter {
    MENTAL_HEALTH_CRISIS_HOTLINE("15770199"),
    HEALTH_AND_WELFARE_CALL_CENTER("129");

    private final String phoneNumber;

    SuggestCallCenter(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
