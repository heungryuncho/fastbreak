package com.portfolio.fastbreak.domain.reservation.repository;

import com.portfolio.fastbreak.domain.reservation.entity.Reservation;
import com.portfolio.fastbreak.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 특정 회원의 전체 예매(취소 포함) 목록 조회
    List<Reservation> findByMemberId(Long memberId);

    // 특정 상태 이면서 특정 시간 이전에 생성된 예약 조회
    List<Reservation> findAllByStatusAndReservedAtBefore(ReservationStatus status, LocalDateTime dateTime);
}
