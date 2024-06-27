package com.backend.dorandoran.contents.repository.querydsl;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import java.util.List;

public interface PsychotherapyContentsQueryRepository {

    List<PsychotherapyContents> findRandomContentsByCategories(List<Disease> diseasesList, int limit);
}
