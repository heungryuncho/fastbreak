package com.portfolio.fastbreak.domain.seat.service;

import com.portfolio.fastbreak.domain.seat.dto.SeatResponse;
import com.portfolio.fastbreak.domain.seat.entity.Seat;
import com.portfolio.fastbreak.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse.SeatInfoResponse> getSeatsByGameId(Long gameId) {
        List<Seat> seats = seatRepository.findByGameIdOrderBySeatNumberAsc(gameId);

        return seats.stream()
                .map(seat -> new SeatResponse.SeatInfoResponse(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getGrade(),
                        seat.getPrice(),
                        seat.getStatus()
                ))
                .collect(Collectors.toList());
    }
}
