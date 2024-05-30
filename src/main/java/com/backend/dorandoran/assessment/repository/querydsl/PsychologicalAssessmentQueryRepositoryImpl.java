package com.backend.dorandoran.assessment.repository.querydsl;

import com.backend.dorandoran.assessment.domain.entity.QPsychologicalAssessmentQuestion;
import com.backend.dorandoran.user.domain.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PsychologicalAssessmentQueryRepositoryImpl implements PsychologicalAssessmentQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QUser user = QUser.user;
    private final QPsychologicalAssessmentQuestion question = QPsychologicalAssessmentQuestion.psychologicalAssessmentQuestion;

    @Override
    public Boolean hasPsychologicalAssessment(Long userId) {
        return null;
    }
}
