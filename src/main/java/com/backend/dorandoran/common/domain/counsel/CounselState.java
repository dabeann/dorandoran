package com.backend.dorandoran.common.domain.counsel;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum CounselState {
    PROCEED_STATE("counsel"),
    FINISH_STATE("complete");

    private final String lowerState;

    CounselState(String lowerState) {
        this.lowerState = lowerState;
    }

    private static final Map<String, CounselState> BY_LOWER_STATE =
            Stream.of(values()).collect(Collectors.toMap(CounselState::getLowerState, e -> e));

    public static CounselState valueOfLowerState(String lowerState) {
        return BY_LOWER_STATE.get(lowerState);
    }
}
