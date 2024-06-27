package com.backend.dorandoran.mypage.repository;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.counsel.domain.entity.QCounsel;
import com.backend.dorandoran.mypage.domain.request.PsychologicalChangeTrendRequest;
import com.backend.dorandoran.mypage.domain.response.CompletedCounselResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.domain.response.PsychologicalChangeTrendResponse;
import com.backend.dorandoran.user.domain.entity.QUser;
import com.backend.dorandoran.user.domain.entity.QUserMentalState;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MypageQueryRepositoryImpl implements MypageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QUser user = QUser.user;
    private final QUserMentalState userMentalState = QUserMentalState.userMentalState;
    private final QCounsel counsel = QCounsel.counsel;

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

    @Override
    public MypageMainResponse getUserInfoForMain(Long userId) {
        return jpaQueryFactory.select(Projections.constructor(MypageMainResponse.class,
                    user.name,
                    new CaseBuilder()
                            .when(userMentalState.id.isNotNull())
                            .then((ComparableExpression<Boolean>) Expressions.asBoolean(true))
                            .otherwise(Expressions.asBoolean(false))
                            .as("hasPsychologicalAssessment")
                ))
                .from(user)
                .leftJoin(userMentalState).on(user.id.eq(userMentalState.user.id))
                .where(user.id.eq(userId))
                .fetchFirst();
    }

    @Override
    public List<PsychologicalChangeTrendResponse> getUserPsychologicalChangeTrend(Long userId, PsychologicalChangeTrendRequest request) {
        NumberTemplate<Integer> dayTemplate =
                Expressions.numberTemplate(Integer.class, "extract(day from {0})", userMentalState.createdDateTime);
        NumberTemplate<Integer> monthTemplate =
                Expressions.numberTemplate(Integer.class, "extract(month from {0})", userMentalState.createdDateTime);

        SimpleExpression<?> category = getCategory(request);

        return jpaQueryFactory.select(Projections.constructor(PsychologicalChangeTrendResponse.class,
                        dayTemplate.as("dayOfMonth"), category
                ))
                .from(userMentalState)
                .where(userMentalState.user.id.eq(userId)
                        .and(monthTemplate.eq(request.month())))
                .offset(1)
                .fetch();
    }

    private SimpleExpression<?> getCategory(PsychologicalChangeTrendRequest request) {
        return switch (request.category()) {
            case ANXIETY -> userMentalState.anxiety;
            case DEPRESSION -> userMentalState.depression;
            case STRESS -> userMentalState.stress;
            case BASIC -> throw new CommonException(ErrorCode.NOT_ALLOWED_CATEGORY);
        };
    }

    @Override
    public List<CompletedCounselResponse> getCompletedCounselList(Long userId, String counselDate) {
        LocalDate targetDate = LocalDate.parse(counselDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.toLocalDate().atTime(23, 59, 59);

        return jpaQueryFactory.select(Projections.constructor(CompletedCounselResponse.class,
                    counsel.id, counsel.title,
                        Expressions.stringTemplate("to_char({0}, 'YYYY-MM-DD')", counsel.updatedDateTime)
                ))
                .from(counsel)
                .where(counsel.user.id.eq(userId)
                        .and(counsel.updatedDateTime.between(startOfDay, endOfDay))
                        .and(counsel.state.eq(CounselState.FINISH_STATE))
                )
                .fetch();
    }
}