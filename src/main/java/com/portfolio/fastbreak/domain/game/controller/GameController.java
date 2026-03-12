package com.portfolio.fastbreak.domain.game.controller;

import com.portfolio.fastbreak.domain.game.dto.GameRequest;
import com.portfolio.fastbreak.domain.game.dto.GameResponse;
import com.portfolio.fastbreak.domain.game.service.GameService;
import com.portfolio.fastbreak.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService gameService;

    // 경기 등록 API
    @PostMapping
    public ApiResponse<GameResponse.GameInfoResponse> createGame(@RequestBody GameRequest.CreateGameRequest request) {
        GameResponse.GameInfoResponse response = gameService.createGame(request);
        return ApiResponse.success(response, "새로운 경기가 등록되었습니다.");
    }

    // 전체 경기 목록 조회 API
    @GetMapping
    public ApiResponse<List<GameResponse.GameInfoResponse>> getAllgames() {
        List<GameResponse.GameInfoResponse> responseList = gameService.getAllGames();
        return ApiResponse.success(responseList, "전체 경기 목록 조회 성공");
    }
}
