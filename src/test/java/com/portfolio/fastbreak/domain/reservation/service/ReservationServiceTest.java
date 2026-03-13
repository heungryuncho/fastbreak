package com.portfolio.fastbreak.domain.reservation.service;

import com.portfolio.fastbreak.domain.game.dto.GameRequest;
import com.portfolio.fastbreak.domain.game.dto.GameResponse;
import com.portfolio.fastbreak.domain.game.service.GameService;
import com.portfolio.fastbreak.domain.member.dto.MemberRequest;
import com.portfolio.fastbreak.domain.member.dto.MemberResponse;
import com.portfolio.fastbreak.domain.member.service.MemberService;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationRequest;
import com.portfolio.fastbreak.domain.reservation.dto.ReservationResponse;
import com.portfolio.fastbreak.domain.reservation.entity.ReservationStatus;
import com.portfolio.fastbreak.domain.seat.dto.SeatResponse;
import com.portfolio.fastbreak.domain.seat.service.SeatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class ReservationServiceTest {

    @Autowired private MemberService memberService;
    @Autowired private GameService gameService;
    @Autowired private SeatService seatService;
    @Autowired private ReservationService reservationService;

    @Test
    @DisplayName("성공적인 예매 흐름 테스트 : 회원가입 -> 경기 등록 -> 1번 좌석 예매")
    void testSuccessfulReservation() {
        // 1. 회원 가입
        MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("test@test.com", "1234", "홍길동");
        MemberResponse.MemberInfoResponse memberInfo = memberService.signup(signUpRequest);

        // 2. 경기 생성
        GameRequest.CreateGameRequest gameRequest = new GameRequest.CreateGameRequest("서울 결승전", LocalDateTime.now().plusDays(3), "잠실체육관");
        GameResponse.GameInfoResponse gameInfo = gameService.createGame(gameRequest);

        // 3. 경기 예매 가능 좌석 조회 및 가장 첫 번째 좌석 ID 획득
        List<SeatResponse.SeatInfoResponse> seats = seatService.getSeatsByGameId(gameInfo.id());
        assertThat(seats).hasSize(50);
        Long targetSeatId = seats.get(0).id();

        // 4. 예매 진행
        ReservationRequest.CreateReservationRequest reserveRequest = new ReservationRequest.CreateReservationRequest(memberInfo.id(), targetSeatId);
        ReservationResponse.ReservationInfoResponse resInfo = reservationService.reserve(reserveRequest);

        // 5. 결과 검증
        assertThat(resInfo.status()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(resInfo.seatNumber()).isEqualTo(1);
        assertThat(resInfo.gameTitle()).isEqualTo("서울 결승전");

        // 6. 내 예매 목록 조회하여 결과 재확인
        List<ReservationResponse.ReservationInfoResponse> myReservations = reservationService.getMyReservations(memberInfo.id());
        assertThat(myReservations).hasSize(1);
    }

    @Test
    @DisplayName("예외 검증 테스트 : 이미 선점된 좌석을 예매 시도하면 예외가 발생")
    void reserveAlreadyReservedSeat_ThrowsException() {
        // 1. 회원 2명 가입 및 경기 생성
        MemberResponse.MemberInfoResponse member1 = memberService.signup(new MemberRequest.SignUpRequest("user1@test.com", "1234", "유저1"));
        MemberResponse.MemberInfoResponse member2 = memberService.signup(new MemberRequest.SignUpRequest("user2@test.com", "1234", "유저2"));
        GameResponse.GameInfoResponse gameInfo = gameService.createGame(new GameRequest.CreateGameRequest("서울 결승전", LocalDateTime.now(), "잠실체육관"));

        // 공통 타겟 좌석 1개 확보
        Long targetSeatId = seatService.getSeatsByGameId(gameInfo.id()).get(0).id();

        // 2. 유저1이 먼저 타겟 좌석 예매 완료
        reservationService.reserve(new ReservationRequest.CreateReservationRequest(member1.id(), targetSeatId));

        // 3. 유저2가 동일한 타겟 좌석 예매 시도 -> 예외 무조건 발생
        assertThatThrownBy(() -> reservationService.reserve(new ReservationRequest.CreateReservationRequest(member2.id(), targetSeatId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 예매된 좌석입니다.");
    }


}
