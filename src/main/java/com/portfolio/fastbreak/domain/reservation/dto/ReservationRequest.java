package com.portfolio.fastbreak.domain.reservation.dto;

public class ReservationRequest {
    public record CreateReservationRequest(Long memberId, Long seatId) {
    }
}
