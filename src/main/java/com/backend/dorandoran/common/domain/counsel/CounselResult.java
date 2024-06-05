package com.backend.dorandoran.common.domain.counsel;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CounselResult {
    GOOD("님의 심리상태가 더 좋아졌어요!"),
    BAD("님, 더 노력해봐요!");

    private final String koreanResult;

    CounselResult(String koreanResult) {
        this.koreanResult = koreanResult;
    }

    public String getKoreanResult() {
        return koreanResult;
    }

    private static final Map<String, CounselResult> BY_KOREAN_RESULT =
            Stream.of(values()).collect(Collectors.toMap(CounselResult::getKoreanResult, e -> e));

    public static CounselResult valueOfKoreanResult(String koreanResult) {
        return BY_KOREAN_RESULT.get(koreanResult);
    }
}
