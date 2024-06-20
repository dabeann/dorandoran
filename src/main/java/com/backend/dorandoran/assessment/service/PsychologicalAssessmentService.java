package com.backend.dorandoran.assessment.service;

import com.backend.dorandoran.assessment.domain.request.PsychologicalAssessmentRequest;
import com.backend.dorandoran.assessment.domain.request.PsychologicalAssessmentRequest.PsychologicalAssessmentQuestionAnswer;
import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentCategory;
import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentStandard;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.contents.domain.entity.Quotation;
import com.backend.dorandoran.contents.repository.QuotationRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import com.backend.dorandoran.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PsychologicalAssessmentService {

    private final UserMentalStateRepository userMentalStateRepository;
    private final UserRepository userRepository;
    private final QuotationRepository quotationRepository;

    private static final String DATE_FORMAT = "yyyy년 MM월 dd일";
    private static final int STANDARD_POINT = 5;
    private static final int MAX_ANSWER_SCORE = 5;
    private static final int PERCENTAGE_SCALE = 100;

    public Boolean hasPsychologicalAssessmentResult() {
        Long userId = UserInfoUtil.getUserIdOrThrow();
        return userMentalStateRepository.existsByUserId(userId);
    }

    @Transactional
    public PsychologicalAssessmentResponse analysisPsychologicalAssessment(List<PsychologicalAssessmentRequest> requests) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<PsychologicalAssessmentResponse.PsychologicalAssessmentResult> results = new ArrayList<>();
        for (PsychologicalAssessmentRequest request: requests) {
            if (!request.category().equals(PsychologicalAssessmentCategory.BASIC)) {
                results.add(analysisMentalState(request));
            } else {
                extractDiseasesAndQuotation(request, user);
            }
        }

        UserMentalState userMentalState = UserMentalState.toUserMentalStateEntity(user, results);
        userMentalStateRepository.save(userMentalState);

        return PsychologicalAssessmentResponse.builder()
                .name(user.getName())
                .testDate(userMentalState.getCreatedDateTime().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .result(results)
                .build();
    }

    private void extractDiseasesAndQuotation(PsychologicalAssessmentRequest request, User user) {
        Set<Disease> diseases = analysisDiseases(request.questionAnswers());
        List<Quotation> quotationsByDiseases = quotationRepository.findAllByCategoryIn(new ArrayList<>(diseases));
        Collections.shuffle(quotationsByDiseases);
        quotationsByDiseases.stream().findFirst().ifPresent(quotation ->
            user.updateDiseasesAndQuotation(diseases.toArray(new Disease[0]), quotation));
    }

    private Set<Disease> analysisDiseases(List<PsychologicalAssessmentQuestionAnswer> answers) {
        Set<Disease> diseases = new HashSet<>();
        analyzeAlcoholism(diseases, answers.get(0));
        analyzeSmokingAddiction(diseases, answers.get(1));
        analyzeImprovements(diseases, answers.get(2));
        return diseases;
    }

    private void analyzeAlcoholism(Set<Disease> diseases, PsychologicalAssessmentQuestionAnswer answer) {
        if (answer.answerId() == 3 || answer.answerId() == 4) {
            diseases.add(Disease.ALCOHOLISM);
        }
    }

    private void analyzeSmokingAddiction(Set<Disease> diseases, PsychologicalAssessmentQuestionAnswer answer) {
        if (answer.answerId() == 3 || answer.answerId() == 4) {
            diseases.add(Disease.SMOKING_ADDICTION);
        }
    }

    private static void analyzeImprovements(Set<Disease> diseases, PsychologicalAssessmentQuestionAnswer answer) {
        String[] hasImprovementAnswerIds = String.valueOf(answer.answerId()).split("");
        for (String answerId : hasImprovementAnswerIds) {
            switch (Integer.parseInt(answerId)) {
                case 1 -> diseases.add(Disease.DEPRESSION);
                case 2 -> diseases.add(Disease.STRESS_OVERLOAD);
                case 3 -> diseases.add(Disease.ANXIETY);
                case 4 -> diseases.add(Disease.ALCOHOLISM);
                case 5 -> diseases.add(Disease.SMOKING_ADDICTION);
            }
        }
    }

    private PsychologicalAssessmentResponse.PsychologicalAssessmentResult analysisMentalState(PsychologicalAssessmentRequest requests) {
        int userTotalPoints = calculateTotalPoints(requests);
        int maxPoints = calculateMaxPoints(requests);
        int percent = calculatePercentage(userTotalPoints, maxPoints);
        PsychologicalAssessmentStandard standard = calculateStandard(userTotalPoints, requests.category());

        return PsychologicalAssessmentResponse.PsychologicalAssessmentResult.builder()
                .category(requests.category())
                .score(PERCENTAGE_SCALE - percent)
                .percent(percent)
                .standard(standard)
                .build();
    }

    private PsychologicalAssessmentStandard calculateStandard(int userTotalPoints, PsychologicalAssessmentCategory category) {
        PsychologicalAssessmentStandard standard = PsychologicalAssessmentStandard.적음;
        int mediumMaxPoint = category.equals(PsychologicalAssessmentCategory.STRESS) ? 21 : 16;
        if (userTotalPoints >= 8 && userTotalPoints < mediumMaxPoint) {
            standard = PsychologicalAssessmentStandard.중간;
        } else if (userTotalPoints >= mediumMaxPoint) {
            standard = PsychologicalAssessmentStandard.심각;
        }
        return standard;
    }

    private static int calculateTotalPoints(PsychologicalAssessmentRequest requests) {
        return requests.questionAnswers().stream().mapToInt(PsychologicalAssessmentQuestionAnswer::answerId).sum();
    }

    private static int calculateMaxPoints(PsychologicalAssessmentRequest requests) {
        return requests.questionAnswers().size() * MAX_ANSWER_SCORE;
    }

    private static int calculatePercentage(int userTotalPoints, int maxPoints) {
        double percent = (double) (userTotalPoints - STANDARD_POINT) / (maxPoints - STANDARD_POINT);
        return (int) Math.round(percent * PERCENTAGE_SCALE);
    }
}