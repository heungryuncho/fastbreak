package com.portfolio.fastbreak.domain.reservation.scheduler;

import com.portfolio.fastbreak.domain.reservation.entity.Reservation;
import com.portfolio.fastbreak.domain.reservation.entity.ReservationStatus;
import com.portfolio.fastbreak.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCleanupScheduler {

    private final ReservationRepository reservationRepository;

    // 1분마다 실행 (미결제 5분 경과 여부 확인)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredReservations() {
        log.info("만료된 예약 정리를 시작하는 중입니다.");

        // 5분 전 시간 계산
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);

        // PENDING 상태이면서 생성된지 5분이 지난 예약들 조회
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndReservedAtBefore(ReservationStatus.PENDING, threshold);

        for (Reservation res : expiredReservations) {
            log.info("만료된 reservationID: {}, Seat: {}", res.getId(), res.getSeat().getSeatNumber());
            res.cancel();
        }
    }
}
