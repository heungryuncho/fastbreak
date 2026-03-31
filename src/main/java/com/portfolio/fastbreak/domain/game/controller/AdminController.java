package com.portfolio.fastbreak.domain.game.controller;

import com.portfolio.fastbreak.domain.game.dto.GameResponse;
import com.portfolio.fastbreak.domain.game.service.GameService;
import com.portfolio.fastbreak.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final GameService gameService;

    // 특정 경기의 예매율 및 매출액 조회 (관리자)
    @GetMapping("/games/{gameId}/stats")
    public ApiResponse<GameResponse.GameStatsResponse> getGameStats(@PathVariable("gameId") Long gameId) {
        GameResponse.GameStatsResponse stats = gameService.getGameStats(gameId);
        return ApiResponse.success(stats, "경기 통계 조회 성공");
    }
}
