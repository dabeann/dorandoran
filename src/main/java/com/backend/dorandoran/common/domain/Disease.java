package com.backend.dorandoran.common.domain;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Disease {
    DEPRESSION("depression"),
    STRESS_OVERLOAD("stress"),
    ANXIETY("anxiety"),
    ALCOHOLISM("alcoholism"),
    SMOKING_ADDICTION("smoking");

    private final String lowercase;

    Disease(String lowercase) {
        this.lowercase = lowercase;
    }

    private static final Map<String, Disease> BY_LOWER_NAME =
            Stream.of(values()).collect(Collectors.toMap(Disease::getLowercase, e -> e));

    public static Disease valueOfKoreanName(String lowercase) {
        return BY_LOWER_NAME.get(lowercase);
    }
}
