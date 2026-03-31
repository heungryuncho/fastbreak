package com.portfolio.fastbreak.domain.reservation.dto;

import com.portfolio.fastbreak.domain.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationResponse {
    public record ReservationInfoResponse(
            Long reservationId,
            String gameTitle,
            Integer seatNumber,
            Integer price,
            ReservationStatus status,
            LocalDateTime reservedAt
    ) {}
}
