package com.portfolio.fastbreak.domain.seat.dto;

import com.portfolio.fastbreak.domain.seat.entity.SeatStatus;

public class SeatResponse {
    public record SeatInfoResponse(Long id, Integer seatNumber, Integer price, SeatStatus status) {
    }
}
