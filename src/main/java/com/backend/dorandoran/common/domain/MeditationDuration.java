package com.backend.dorandoran.common.domain;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final Map<Integer, MeditationDuration> BY_MINUTES =
            Stream.of(values()).collect(Collectors.toMap(MeditationDuration::getMinutes, e -> e));

    public static MeditationDuration valueOfMinutes(Integer minutes) {
        return BY_MINUTES.get(minutes);
    }
}
