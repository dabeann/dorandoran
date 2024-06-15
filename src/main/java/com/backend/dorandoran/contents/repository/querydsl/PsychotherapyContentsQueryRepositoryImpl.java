package com.backend.dorandoran.contents.repository.querydsl;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.domain.entity.QPsychotherapyContents;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PsychotherapyContentsQueryRepositoryImpl implements PsychotherapyContentsQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QPsychotherapyContents psychotherapyContents = QPsychotherapyContents.psychotherapyContents;

    @Override
    public List<PsychotherapyContents> findRandomContentsByCategories(List<Disease> diseasesList, int limit) {
        return jpaQueryFactory.selectFrom(psychotherapyContents)
                .where(psychotherapyContents.category.in(diseasesList))
                .orderBy(Expressions.numberTemplate(Double.class, "function('RANDOM')").asc())
                .limit(limit)
                .fetch();
    }
}
