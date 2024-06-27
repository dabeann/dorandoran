package com.backend.dorandoran.mypage.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MypageSwaggerConfig {

    @Bean
    GroupedOpenApi mypagerDocs() {
        return GroupedOpenApi.builder()
                .group("마이페이지 API")
                .packagesToScan("com.backend.dorandoran.mypage.controller")
                .build();
    }
}
