package com.backend.dorandoran.common.domain.counsel;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum CounselState {
    PROCEED_STATE("진행중"),
    FINISH_STATE("종료");

    private final String koreanState;

    CounselState(String koreanState) {
        this.koreanState = koreanState;
    }

    private static final Map<String, CounselState> BY_KOREAN_STATE =
            Stream.of(values()).collect(Collectors.toMap(CounselState::getKoreanState, e -> e));

    public static CounselState valueOfKoreanState(String koreanState) {
        return BY_KOREAN_STATE.get(koreanState);
    }
}
