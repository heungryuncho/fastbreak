package com.portfolio.fastbreak.domain.reservation.service;

import com.portfolio.fastbreak.domain.member.entity.Member;
import com.portfolio.fastbreak.domain.member.repository.MemberRepository;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationRequest;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationResponse;
import com.portfolio.fastbreak.domain.reservation.entity.Reservation;
import com.portfolio.fastbreak.domain.reservation.entity.ReservationStatus;
import com.portfolio.fastbreak.domain.reservation.repository.ReservationRepository;
import com.portfolio.fastbreak.domain.seat.dto.SeatResponse;
import com.portfolio.fastbreak.domain.seat.entity.Seat;
import com.portfolio.fastbreak.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;
    private final SimpMessagingTemplate messagingTemplate;

    // 1. 예매 하기
    @Transactional
    public ReservationResponse.ReservationInfoResponse reserve(ReservationRequest.CreateReservationRequest request, String email) {
        // 좌석 ID별로 고유한 락 키 생성
        String lockKey = "lock:seat:" + request.seatId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 10초 동안 락 획득 시도, 락 획득 후 1초간 점유
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                throw new RuntimeException("락을 획득할 수 없습니다. 다시 시도해 주세요.");
            }

            // 1. 회원 검증
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            // 2. 좌석 검증 (Redis 분산 락 안에서 수행)
            Seat seat = seatRepository.findById(request.seatId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

            // 3. 좌석 선점
            seat.reserve();

            // 4. 예매 내역 생성
            Reservation reservation = Reservation.builder()
                    .member(member)
                    .seat(seat)
                    .status(ReservationStatus.PENDING)
                    .build();

            // 5. DB 저장
            Reservation savedReservation = reservationRepository.save(reservation);

            // 6. 실시간 업데이트
            Long gameId = seat.getGame().getId();
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/seats",
                    new SeatResponse.SeatInfoResponse(
                            seat.getId(),
                            seat.getSeatNumber(),
                            seat.getGrade(),
                            seat.getPrice(),
                            seat.getStatus()
                    )
            );

            // 7. 응답 반환
            return new ReservationResponse.ReservationInfoResponse(
                    savedReservation.getId(),
                    savedReservation.getSeat().getGame().getTitle(),
                    savedReservation.getSeat().getSeatNumber(),
                    savedReservation.getSeat().getPrice(),
                    savedReservation.getStatus(),
                    savedReservation.getReservedAt()
            );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 2. 내 예매 목록 조회
    @Transactional(readOnly = true)
    public List<ReservationResponse.ReservationInfoResponse> getMyReservations(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<Reservation> reservations = reservationRepository.findByMemberId(member.getId());

        return reservations.stream()
                .map(res -> new ReservationResponse.ReservationInfoResponse(
                        res.getId(),
                        res.getSeat().getGame().getTitle(),
                        res.getSeat().getSeatNumber(),
                        res.getSeat().getPrice(),
                        res.getStatus(),
                        res.getReservedAt()
                ))
                .collect(Collectors.toList());
    }

    // 3. 결제 완료
    @Transactional
    public void completePayment(Long reservationId, String email) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 예약입니다."));

        if (!reservation.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 예약만 결제할 수 있습니다.");
        }

        reservation.complete(); // 상태 변경 (PENDING -> COMPLETED)
    }

    // 4. 예매 취소
    @Transactional
    public void cancelReservation(Long reservationId, String email) {
        // 예약 내역 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 본인 확인
        if (!reservation.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }

        // 예약 취소 처리
        reservation.cancel();
    }
}
