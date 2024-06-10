package com.backend.dorandoran.common.domain.counsel;

public enum SuggestCallCenter {
    MENTAL_HEALTH_CRISIS_HOTLINE("15770199"),
    HEALTH_AND_WELFARE_CALL_CENTER("129");

    private final String phoneNumber;

    SuggestCallCenter(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
