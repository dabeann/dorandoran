package com.backend.dorandoran.common.domain;

public enum MeditationDuration {
    THREE_MINUTES(3),
    FIVE_MINUTES(5),
    TEN_MINUTES(10),
    THIRTY_MINUTES(30),
    ONE_HOUR(60);

    private final int minutes;

    MeditationDuration(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
}
