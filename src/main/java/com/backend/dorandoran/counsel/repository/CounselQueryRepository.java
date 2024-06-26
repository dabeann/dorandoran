package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.counsel.domain.response.CounselHistoryResponse.CounselHistory;
import java.util.List;

public interface CounselQueryRepository {

    List<CounselHistory> getCounselHistoryByState(CounselState counselState, Long userId);
}
