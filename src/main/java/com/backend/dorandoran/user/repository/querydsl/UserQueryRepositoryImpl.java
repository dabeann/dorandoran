package com.backend.dorandoran.user.repository.querydsl;

import com.backend.dorandoran.counsel.domain.entity.QCounsel;
import com.backend.dorandoran.counsel.domain.entity.QDialog;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QCounsel counsel = QCounsel.counsel;
    private final QDialog dialog = QDialog.dialog;

    @Override
    public void deleteCounselAndDialogByUserId(Long userId) {
        jpaQueryFactory.delete(dialog)
                    .where(counsel.id.in(
                            JPAExpressions.select(dialog.counsel.id)
                                .from(dialog)
                                .where(dialog.counsel.user.id.eq(userId)))
                    ).execute();
        jpaQueryFactory.delete(counsel)
                .where(counsel.user.id.eq(userId))
                .execute();
    }
}