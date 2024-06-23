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
import org.springframework.data.domain.PageRequest;
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
                .temperature(0.2)
                .topP(0.2)
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

        String systemMessage = "당신은 노숙인 " + user.getName() + "님의 담당 상담사입니다. 당신은 노숙인 " + user.getName()
                + "님의 감정을 안정화하도록 돕는 역할입니다. 센터 등 자원을 찾아주거나 다른 곳과 연결한다고 말하지 않습니다. "
                + "여러분, 당신이라는 단어를 절대 사용하지 않습니다.";
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        String assistantMessage = "반드시 최근 " + history + "를 기반으로 문맥이 이어지게 상담합니다. 내담자, 당신 대신 "
                + user.getName()
                + "로 호칭합니다. 해결이 가능한 고민에 대해서는 해결책을 제안합니다. 다음과 같은 상담 기법을 상황에 맞게 적절하게 사용합니다. "
                + "한 기법만 반복하지 않습니다. 1. 미러링 : " + user.getName() + "님의 말을 비슷한 의미의 문장으로 변화해서 말해줍니다. "
                + "2. 자기공개 : " + user.getName() + "이 말한 상황과 비슷한 상황을 경험한 내용을 말하여 공감을 이끌어냅니다. "
                + "가끔씩 대화 중간에 내담자의 자존감을 놉여줄 수 있는 문장을 말합니다.";
        ChatMessage assistantMsg = new ChatMessage(ChatMessageRole.ASSISTANT.toString().toLowerCase(), assistantMessage);
        messages.add(assistantMsg);

        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.toString().toLowerCase(), message);
        messages.add(userMsg);

        return messages;
    }

    private String getChatGptResponse(Counsel counsel, String userMessage, User user) {
        PageRequest pageRequest = PageRequest.of(0, 4);
        List<Dialog> previousConversations = dialogRepository.findAllByCounselOrderByCreatedDateTimeAsc(counsel, pageRequest);
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

        String systemMessage = "자살, 자해 등과 같이 사용자 메시지에 위급상황으로 판단되는 문맥 또는 단어가 발견되면 1, "
                + "그렇지 않다면 0을 반환해: " + userMessage;
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.toString().toLowerCase(), systemMessage);
        messages.add(systemMsg);

        return messages;
    }

    private void checkEmergencyAndSendSms(String userMessage, User user) {
        while (true) {
            List<ChatMessage> chatMessages = checkEmergencyStatus(userMessage);
            String flag = sendOpenAIRequest4o(chatMessages).getChoices().get(0).getMessage().getContent();

            if (flag.equals("1")) {
                smsUtil.sendEmergencySms(user.getUserAgency().getPhoneNumber(), user.getName(), user.getPhoneNumber());
                break;
            } else if (flag.equals("0")) {
                break;
            }
        }
    }
}
