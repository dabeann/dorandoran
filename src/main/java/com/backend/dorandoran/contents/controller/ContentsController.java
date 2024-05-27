package com.backend.dorandoran.contents.controller;

import com.backend.dorandoran.contents.service.ContentsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contents", description = "콘텐츠 관련 API입니다.")
@RequestMapping("/api/contents")
@RequiredArgsConstructor
@RestController
class ContentsController {

    private final ContentsService contentsService;
}