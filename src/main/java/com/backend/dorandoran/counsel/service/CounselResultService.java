package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.CounselResult;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.config.GptConfig;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.repository.querydsl.PsychotherapyContentsQueryRepository;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.counsel.repository.DialogRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import com.backend.dorandoran.user.repository.UserRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CounselResultService {

    private final DialogRepository dialogRepository;
    private final CounselRepository counselRepository;
    private final UserRepository userRepository;
    private final UserMentalStateRepository userMentalStateRepository;
    private final PsychotherapyContentsQueryRepository psychotherapyContentsQueryRepository;
    private final ApplicationContext applicationContext;

    private final OpenAiService openAiService;

    @Transactional
    public CounselResultResponse endCounsel(Long counselId) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        List<Disease> diseasesList = List.of(user.getDiseases());

        List<PsychotherapyContents> limitThreeContents = psychotherapyContentsQueryRepository
                .findRandomContentsByCategories(diseasesList, 3);

        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));

        List<Dialog> history = dialogRepository.findAllByCounselOrderByCreatedDateTimeAsc(counsel);

        if (counsel.getState() == CounselState.FINISH_STATE) {
            return new CounselResultResponse(counsel.getResult(), counsel.getSummary(),
                    history, limitThreeContents);
        }

        CounselResultService counselResultService = applicationContext.getBean(CounselResultService.class);

        String summary = counselResultService.updateCounselSummary(counsel, history);
        String result = counselResultService.updatePsychologyScoreAndGetResult(counsel, user, history);
        counsel.updateState(CounselState.FINISH_STATE);

        return new CounselResultResponse(result, summary,
                history, limitThreeContents);
    }

    private ChatCompletionResult sendOpenAIRequest4o(List<ChatMessage> prompt) {
        ChatCompletionRequest build = ChatCompletionRequest.builder()
                .messages(prompt)
                .maxTokens(GptConfig.MAX_TOKEN)
                .temperature(GptConfig.TEMPERATURE)
                .topP(GptConfig.TOP_P)
                .model("gpt-4o")
                .build();

        return openAiService.createChatCompletion(build);
    }

    @Transactional
    public String updateCounselSummary(Counsel counsel, List<Dialog> dialogHistory) {
        String history = getDialogHistory(dialogHistory);

        try {
            List<ChatMessage> chatMessages = generatedSummaryChatMessage(history);
            String summary = sendOpenAIRequest4o(chatMessages).getChoices().get(0).getMessage().getContent();
            counsel.updateSummary(summary);
            return summary;
        } catch (Exception e) {
            log.error("Error", e);
            throw new CommonException(ErrorCode.GPT_ERROR);
        }
    }

    @NotNull
    private String getDialogHistory(List<Dialog> previousConversations) {
        return previousConversations.stream()
                .map(conv -> String.format("{\"role\": \"%s\", \"content\": \"%s\"}", conv.getRole(),
                        conv.getContents()))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    @Transactional
    public String updatePsychologyScoreAndGetResult(Counsel counsel, User user, List<Dialog> dialogHistory) {
        String history = getDialogHistory(dialogHistory);

        try {
            List<ChatMessage> chatMessages = generatedPsychologyScoreChatMessage(history);
            String stringScores = sendOpenAIRequest4o(chatMessages).getChoices().get(0).getMessage().getContent();

            int[] scores = Arrays.stream(stringScores.trim().split(","))
                    .mapToInt(s -> Integer.parseInt(s.trim())).toArray();
            int totalScore = Arrays.stream(scores).sum();

            saveNewUserMentalState(user, scores);
            String result = getResult(user, totalScore);
            counsel.updateResult(result);

            return result;
        } catch (Exception e) {
            log.error("Error", e);
            throw new CommonException(ErrorCode.GPT_ERROR);
        }
    }

    @NotNull
    private static String getResult(User user, int totalScore) {
        String result = totalScore >= 0 ? CounselResult.GOOD.getKoreanResult() : CounselResult.BAD.getKoreanResult();
        result = user.getName() + result;
        return result;
    }

    private void saveNewUserMentalState(User user, int[] scores) {
        UserMentalState previousMentalState = userMentalStateRepository.findFirstByUserOrderByCreatedDateTimeDesc(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MENTAL_STATE));
        UserMentalState userMentalState = UserMentalState.toUserMentalStateEntity(user, previousMentalState, scores);
        userMentalStateRepository.save(userMentalState);
    }

    private List<ChatMessage> generatedSummaryChatMessage(String history) {
        List<ChatMessage> messages = new ArrayList<>();

        String systemMessage = "다음은 내담자와 상담자의 대화입니다. 이 대화의 내용을 150글자 이내로 간단히 요약해주세요.:" + history;
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        return messages;
    }

    private List<ChatMessage> generatedPsychologyScoreChatMessage(String history) {
        List<ChatMessage> messages = new ArrayList<>();

        String systemMessage = "대화 내역을 바탕으로 우울, 스트레스, 불안 점수를 추출합니다.";
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        String assistantMessage = "1,-2,3 형태로 추출합니다. 점수는 -3~3점 중 정수로 추출합니다. 상태가 호전된다면 양수, 악화된다면 음수로 추출합니다.";
        ChatMessage assistantMsg = new ChatMessage(ChatMessageRole.ASSISTANT.toString().toLowerCase(), assistantMessage);
        messages.add(assistantMsg);

        String userMessage = history + "를 기반으로 점수를 추출합니다. 우울, 스트레스, 불안 점수를 주어진 형식에 맞게 정확히 추출합니다.";
        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.toString().toLowerCase(), userMessage);
        messages.add(userMsg);

        return messages;
    }
}
