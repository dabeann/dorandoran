package com.backend.dorandoran.common.domain.response;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 호출 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 API"),
        @ApiResponse(responseCode = "403", description = "인가 실패(권한 없음)"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
})
public @interface BasicApiSwaggerResponse {
}
