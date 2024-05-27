package com.backend.dorandoran.common.domain;

public enum Disease {
    DEPRESSION(1),
    STRESS_OVERLOAD(2),
    ANXIETY(3),
    ALCOHOLISM(4),
    SMOKING_ADDICTION(5);

    private final int priority;

    Disease(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
