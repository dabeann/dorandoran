package com.backend.dorandoran.contents.service;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.MeditationDuration;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.contents.domain.entity.MeditationContents;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.domain.entity.Quotation;
import com.backend.dorandoran.contents.domain.response.ContentsResponse;
import com.backend.dorandoran.contents.domain.response.MeditationResponse;
import com.backend.dorandoran.contents.repository.MeditationContentsRepository;
import com.backend.dorandoran.contents.repository.PsychotherapyContentsRepository;
import com.backend.dorandoran.contents.repository.QuotationRepository;
import com.backend.dorandoran.contents.repository.querydsl.PsychotherapyContentsQueryRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class ContentsService {

    private final UserRepository userRepository;
    private final QuotationRepository quotationRepository;
    private final PsychotherapyContentsRepository psychotherapyContentsRepository;
    private final PsychotherapyContentsQueryRepository psychotherapyContentsQueryRepository;
    private final MeditationContentsRepository meditationContentsRepository;

    @Transactional
    public void updateTodayQuotation() {
        List<User> usersWithDiseases = userRepository.findAllByDiseasesNotNull();
        for (User user : usersWithDiseases) {
            List<Quotation> quotationsByDiseases = quotationRepository.findAllByCategoryIn(List.of(user.getDiseases()));
            Collections.shuffle(quotationsByDiseases);
            user.updateTodayQuotation(quotationsByDiseases.stream().findFirst().get());
        }
    }

    public ContentsResponse getMainContents(String category) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        String quotation = null;
        if (user.getDiseases() != null) {
            quotation = user.getTodayQuotation().getContents();
        }

        List<PsychotherapyContents> psychotherapyContentsList = getPsychotherapyContentsList(user, category);

        return new ContentsResponse(quotation, psychotherapyContentsList);
    }

    private List<PsychotherapyContents> getPsychotherapyContentsList(User user, String category) {
        if (user.getDiseases() == null && category == null) {
            return getPsychotherapyContentsListWithCategory(Disease.DEPRESSION.getKoreanName());
        } else if (category == null) {
            List<Disease> diseasesList = List.of(user.getDiseases());
            return getPersonalizedPsychotherapyContentsList(diseasesList);
        } else {
            return getPsychotherapyContentsListWithCategory(category);
        }
    }

    private List<PsychotherapyContents> getPsychotherapyContentsListWithCategory(String category) {
        Disease disease = Disease.valueOfKoreanName(category);
        CommonValidator.notNullOrThrow(disease, ErrorCode.NOT_FOUND_DISEASE);
        return psychotherapyContentsRepository.findAllByCategory(disease);
    }

    private List<PsychotherapyContents> getPersonalizedPsychotherapyContentsList(List<Disease> diseaseList) {
        return psychotherapyContentsQueryRepository.findRandomContentsByCategories(diseaseList, 5);
    }

    public MeditationResponse getMeditationContent(Integer duration) {
        UserInfoUtil.getUserIdOrThrow();
        MeditationDuration meditationDuration = MeditationDuration.valueOfMinutes(duration);
        CommonValidator.notNullOrThrow(meditationDuration, ErrorCode.NOT_FOUND_MEDITATION_DURATION);
        MeditationContents meditationContents = meditationContentsRepository.findFirstByDurationCategory(
                meditationDuration);
        return new MeditationResponse(meditationContents);
    }
}