package com.portfolio.fastbreak.domain.seat.dto;

import com.portfolio.fastbreak.domain.seat.entity.SeatGrade;
import com.portfolio.fastbreak.domain.seat.entity.SeatStatus;

public class SeatResponse {
    public record SeatInfoResponse(
            Long id,
            Integer seatNumber,
            SeatGrade grade,
            Integer price,
            SeatStatus status
    ) {}
}
