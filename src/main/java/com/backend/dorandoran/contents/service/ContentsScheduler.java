package com.backend.dorandoran.contents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ContentsScheduler {

    private final ContentsService contentsService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateTodayQuotation() {
        contentsService.updateTodayQuotation();
    }
}
