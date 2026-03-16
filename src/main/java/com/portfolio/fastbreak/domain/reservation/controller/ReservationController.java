package com.portfolio.fastbreak.domain.reservation.controller;

import com.portfolio.fastbreak.domain.reservation.dto.ReservationRequest;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationResponse;
import com.portfolio.fastbreak.domain.reservation.service.ReservationService;
import com.portfolio.fastbreak.global.common.ApiResponse;
import com.portfolio.fastbreak.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // 1. 경기 예매 요청
    @PostMapping
    public ApiResponse<ReservationResponse.ReservationInfoResponse> reserveSeat(@RequestBody ReservationRequest.CreateReservationRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReservationResponse.ReservationInfoResponse response = reservationService.reserve(request, userDetails.getUsername());
        return ApiResponse.success(response, "예매가 성공적으로 완료되었습니다.");
    }

    // 2. 내 예매 내역 조회
    @GetMapping("/me")
    public ApiResponse<List<ReservationResponse.ReservationInfoResponse>> getMyReservations(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<ReservationResponse.ReservationInfoResponse> responseList = reservationService.getMyReservations(customUserDetails.getUsername());
        return ApiResponse.success(responseList, "내 예매 내역 조회 성공");
    }

    // 3. 예매 취소
    @PatchMapping("/{reservationId}/cancel")
    public ApiResponse<Void> cancelReservation(@PathVariable("reservationId") Long reservationId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        reservationService.cancelReservation(reservationId, userDetails.getUsername());
        return ApiResponse.success(null, "예매가 취소되었습니다.");
    }
}
