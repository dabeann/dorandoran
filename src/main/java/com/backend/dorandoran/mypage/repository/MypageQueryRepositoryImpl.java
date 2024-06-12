package com.backend.dorandoran.mypage.repository;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.user.domain.entity.QUser;
import com.backend.dorandoran.user.domain.entity.QUserMentalState;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MypageQueryRepositoryImpl implements MypageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QUser user = QUser.user;
    private final QUserMentalState userMentalState = QUserMentalState.userMentalState;

    @Override
    public PsychologicalAssessmentResponse getUserFirstAssessmentResult(Long userId) {
        return jpaQueryFactory.select(Projections.constructor(PsychologicalAssessmentResponse.class,
                    user.name, userMentalState.createdDateTime.as("testDate"),
                    userMentalState.anxiety, userMentalState.depression, userMentalState.stress
                ))
                .from(user)
                .innerJoin(userMentalState).on(user.id.eq(userMentalState.user.id))
                .where(user.id.eq(userId))
                .orderBy(userMentalState.id.asc())
                .limit(1)
                .fetchOne();
    }
}
