package com.portfolio.fastbreak.domain.reservation.repository;

import com.portfolio.fastbreak.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 특정 회원의 전체 예매(취소 포함) 목록 조회
    List<Reservation> findByMemberId(Long memberId);
}
