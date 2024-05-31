package com.backend.dorandoran.common.domain;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Disease {
    DEPRESSION("우울증"),
    STRESS_OVERLOAD("스트레스"),
    ANXIETY("불안증"),
    ALCOHOLISM("알코올_중독"),
    SMOKING_ADDICTION("흡연_중독");

    private final String koreanName;

    Disease(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    private static final Map<String, Disease> BY_KOREAN_NAME =
            Stream.of(values()).collect(Collectors.toMap(Disease::getKoreanName, e -> e));

    public static Disease valueOfKoreanName(String koreanName) {
        return BY_KOREAN_NAME.get(koreanName);
    }
}
