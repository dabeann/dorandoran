package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.domain.dialog.DialogRole;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.config.GptConfig;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import com.backend.dorandoran.counsel.domain.request.ChatRequest;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.counsel.repository.DialogRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.repository.UserRepository;
import com.backend.dorandoran.user.service.SmsUtil;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PropertySource("classpath:application-gpt.yml")
@RequiredArgsConstructor
@Service
@Slf4j
public class CounselChatService {

    @Value("${MODEL_NAME}")
    private String MODEL_NAME;

    private final DialogRepository dialogRepository;
    private final CounselRepository counselRepository;
    private final UserRepository userRepository;
    private final SmsUtil smsUtil;
    private final ApplicationContext applicationContext;

    private final OpenAiService openAiService;


    @Transactional
    public String getChatResult(ChatRequest request) {
        Long counselId = request.counselId();
        String userMessage = request.message();

        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));
        if (counsel.getState() == CounselState.FINISH_STATE) {
            throw new CommonException(ErrorCode.ALREADY_CLOSED_COUNSEL);
        }

        String gptResponse = getChatGptResponse(counsel, request.message(), user);

        CounselChatService counselGptService = applicationContext.getBean(CounselChatService.class);
        counselGptService.makeTitleOfCounsel(counsel, userMessage, gptResponse, user);

        checkEmergencyAndSendSms(userMessage, user);

        dialogRepository.save(
                Dialog.builder().counsel(counsel).role(DialogRole.FROM_USER).contents(userMessage).build());
        dialogRepository.save(
                Dialog.builder().counsel(counsel).role(DialogRole.FROM_CONSULTANT).contents(gptResponse).build());

        counsel.updateUpdatedDateNow();

        return gptResponse;
    }

    private ChatCompletionResult sendOpenAIRequest(List<ChatMessage> prompt) {
        ChatCompletionRequest build = ChatCompletionRequest.builder()
                .messages(prompt)
                .maxTokens(GptConfig.MAX_TOKEN)
                .temperature(GptConfig.TEMPERATURE)
                .topP(GptConfig.TOP_P)
                .model(MODEL_NAME)
                .build();

        return openAiService.createChatCompletion(build);
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

    private List<ChatMessage> generatedQuestionAndAnswerMessage(String message, String history, User user) {
        List<ChatMessage> messages = new ArrayList<>();

        String systemMessage = "당신은 노숙인을 대상으로 심리상담을 제공하는 상담봇입니다. "
                + "노숙인 정보 : 이름 : " + user.getName() + "., "
                + "미러링, 자기공개, 요약 등을 통해 심리상담합니다. 제공한 노숙인 정보를 바탕으로 "
                + "개인화된 상담을 진행합니다. 대화중에 내담자, 당신 단어 대신에 노숙인의 이름을 말해줍니다.";
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        ChatMessage assistantMsg = new ChatMessage(ChatMessageRole.ASSISTANT.toString().toLowerCase(), history);
        messages.add(assistantMsg);

        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.toString().toLowerCase(), message);
        messages.add(userMsg);

        return messages;
    }

    private String getChatGptResponse(Counsel counsel, String userMessage, User user) {
        List<Dialog> previousConversations = dialogRepository.findAllByCounselOrderByCreatedDateTimeAsc(counsel);
        String history = previousConversations.stream()
                .map(conv -> String.format("{\"role\": \"%s\", \"content\": \"%s\"}", conv.getRole(),
                        conv.getContents()))
                .collect(Collectors.joining(", ", "[", "]"));

        try {

            List<ChatMessage> chatMessages = generatedQuestionAndAnswerMessage(userMessage, history, user);
            return sendOpenAIRequest(chatMessages).getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("Error", e);
            throw new CommonException(ErrorCode.GPT_ERROR);
        }
    }

    @Transactional
    public void makeTitleOfCounsel(Counsel counsel, String userMessage, String gptMessage, User user) {
        if (!dialogRepository.existsByCounselAndRole(counsel, DialogRole.FROM_USER)) {
            List<ChatMessage> chatMessages = generateTitleOfCounselPrompt(userMessage, gptMessage);
            while (true) {
                String title = sendOpenAIRequest4o(chatMessages).getChoices().get(0).getMessage().getContent();
                Long countByTitle = counselRepository.countByTitleAndUser(title, user);
                if (countByTitle == 0) {
                    counsel.updateTitle(title);
                    break;
                }
            }

        }
    }

    private List<ChatMessage> generateTitleOfCounselPrompt(String userMessage, String gptMessage) {
        List<ChatMessage> messages = new ArrayList<>();

        String systemMessage = "다음 사용자 메시지와 상담자의 응답을 기반으로 상담 제목을 아주 짧게 생성해주세요. \n"
                                + "명사로 끝맺어주세요.\n"
                                + "사용자 메시지: " + userMessage
                                + "\n상담자 응답: " + gptMessage + "\n상담명:";
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        return messages;
    }

    private List<ChatMessage> checkEmergencyStatus(String userMessage) {
        List<ChatMessage> messages = new ArrayList<>();

        String systemMessage = "다음 사용자 메시지가 위급상황인지 판단해서 위급하면 1, 그렇지 않다면 0을 반환해줘.: " + userMessage;
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        return messages;
    }

    private void checkEmergencyAndSendSms(String userMessage, User user) {
        List<ChatMessage> chatMessages = checkEmergencyStatus(userMessage);
        String flag = sendOpenAIRequest4o(chatMessages).getChoices().get(0).getMessage().getContent();

        if (flag.equals("1")) {
            smsUtil.sendEmergencySms(user.getUserAgency().getPhoneNumber(), user.getName(), user.getPhoneNumber());
        }
    }
}
