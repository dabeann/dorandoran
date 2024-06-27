package com.backend.dorandoran.common.domain.counsel;

import lombok.Getter;

@Getter
public enum SuggestComment {
    UNSTABLE("님, 심리상태가 불안정한 것 같아요. 전문적인 상담을 받아보시는 것을 추천드려요."),
    STABLE("");

    private final String koreanComment;

    SuggestComment(String koreanComment) {
        this.koreanComment = koreanComment;
    }
}
