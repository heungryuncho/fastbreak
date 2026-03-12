package com.portfolio.fastbreak.domain.seat.repository;

import com.portfolio.fastbreak.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    // 특정 경기에 속한 모든 좌석을 조회
    List<Seat> findByGameId(Long gameId);
}
