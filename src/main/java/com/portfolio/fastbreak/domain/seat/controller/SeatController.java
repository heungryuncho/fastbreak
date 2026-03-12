package com.portfolio.fastbreak.domain.seat.controller;

import com.portfolio.fastbreak.domain.seat.dto.SeatResponse;
import com.portfolio.fastbreak.domain.seat.service.SeatService;
import com.portfolio.fastbreak.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/{gameId}/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ApiResponse<List<SeatResponse.SeatInfoResponse>> getSeats(@PathVariable("gameId") Long gameId) {
        List<SeatResponse.SeatInfoResponse> responseList = seatService.getSeatsByGameId(gameId);
        return ApiResponse.success(responseList, "좌석 목록 조회 성공");
    }
}
