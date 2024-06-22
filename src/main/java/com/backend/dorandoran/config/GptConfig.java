package com.backend.dorandoran.config;

import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-gpt.yml")
public class GptConfig {

    @Value("${OPENAI_API_KEY}")
    private String openaiApiKey;

    public static final double TOP_P = 1.0;

    public static final int MAX_TOKEN = 2000;

    public static final double TEMPERATURE = 1.0;

    public static final Duration TIME_OUT = Duration.ofSeconds(300);

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey, TIME_OUT);
    }
}
