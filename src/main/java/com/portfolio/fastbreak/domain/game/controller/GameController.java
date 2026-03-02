package com.portfolio.fastbreak.domain.game.controller;

import com.portfolio.fastbreak.global.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    // 1. 정상 응답 테스트용 API
    @GetMapping("/health")
    public ApiResponse<String> checkHealth() {
        return ApiResponse.success("Fastbrerak API Server is running!");
    }

    // 2. 예외 처리 동작 테스트용 API
    @GetMapping("/error-test")
    public ApiResponse<Void> testError() {
        throw new RuntimeException("전역 예외 처리 테스트용 강제 에러 발생");
    }
}
