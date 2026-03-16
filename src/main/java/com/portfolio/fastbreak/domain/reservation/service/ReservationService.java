package com.portfolio.fastbreak.domain.reservation.service;

import com.portfolio.fastbreak.domain.member.entity.Member;
import com.portfolio.fastbreak.domain.member.repository.MemberRepository;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationRequest;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationResponse;
import com.portfolio.fastbreak.domain.reservation.entity.Reservation;
import com.portfolio.fastbreak.domain.reservation.entity.ReservationStatus;
import com.portfolio.fastbreak.domain.reservation.repository.ReservationRepository;
import com.portfolio.fastbreak.domain.seat.entity.Seat;
import com.portfolio.fastbreak.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;

    // 1. 예매 하기
    @Transactional
    public ReservationResponse.ReservationInfoResponse reserve(ReservationRequest.CreateReservationRequest request, String email) {
        // 1. 회원 검증
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 좌석 검증
        Seat seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        // 3. 좌석 선점
        seat.reserve();

        // 4. 예매 내역 생성
        Reservation reservation = Reservation.builder()
                .member(member)
                .seat(seat)
                .status(ReservationStatus.COMPLETED)
                .build();

        // 5. DB 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        // 6. 응답 반환
        return new ReservationResponse.ReservationInfoResponse(
                savedReservation.getId(),
                savedReservation.getSeat().getGame().getTitle(),
                savedReservation.getSeat().getSeatNumber(),
                savedReservation.getSeat().getPrice(),
                savedReservation.getStatus(),
                savedReservation.getReservedAt()
        );
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

    // 3. 예매 취소
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
