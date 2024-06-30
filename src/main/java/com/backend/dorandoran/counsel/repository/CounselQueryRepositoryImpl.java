package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.counsel.domain.entity.QCounsel;
import com.backend.dorandoran.counsel.domain.entity.QDialog;
import com.backend.dorandoran.counsel.domain.response.CounselHistoryResponse.CounselHistory;
import com.backend.dorandoran.user.domain.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CounselQueryRepositoryImpl implements CounselQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QUser user = QUser.user;
    private final QCounsel counsel = QCounsel.counsel;
    private final QDialog dialog = QDialog.dialog;

    @Override
    public List<CounselHistory> getCounselHistoryByState(CounselState counselState, Long userId) {
        return jpaQueryFactory.select(Projections.constructor(CounselHistory.class,
                counsel.id, counsel.title,
                        Expressions.stringTemplate("to_char({0}, 'YYYY-MM-DD')", dialog.createdDateTime.max())))
                .from(user)
                .innerJoin(counsel).on(user.id.eq(userId))
                .innerJoin(dialog).on(dialog.counsel.eq(counsel))
                .where(user.id.eq(userId)
                        .and(counsel.user.eq(user))
                        .and(counsel.state.eq(counselState)))
                .groupBy(counsel.id)
                .orderBy(dialog.createdDateTime.max().desc())
                .fetch();
    }
}
