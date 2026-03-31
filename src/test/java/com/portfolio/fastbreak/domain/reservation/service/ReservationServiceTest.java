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
import com.portfolio.fastbreak.domain.seat.entity.SeatGrade;
import com.portfolio.fastbreak.domain.seat.service.SeatService;
import com.portfolio.fastbreak.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired private MemberService memberService;
    @Autowired private GameService gameService;
    @Autowired private SeatService seatService;
    @Autowired private ReservationService reservationService;

    @Test
    @Transactional
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
        ReservationRequest.CreateReservationRequest reserveRequest = new ReservationRequest.CreateReservationRequest(targetSeatId);
        ReservationResponse.ReservationInfoResponse resInfo = reservationService.reserve(reserveRequest, "test@test.com");

        // 5. 결과 검증
        assertThat(resInfo.status()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(resInfo.seatNumber()).isEqualTo(1);
        assertThat(resInfo.gameTitle()).isEqualTo("서울 결승전");

        // 6. 내 예매 목록 조회하여 결과 재확인
        List<ReservationResponse.ReservationInfoResponse> myReservations = reservationService.getMyReservations("test@test.com");
        assertThat(myReservations).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("예외 검증 테스트 : 이미 선점된 좌석을 예매 시도하면 예외가 발생")
    void reserveAlreadyReservedSeat_ThrowsException() {
        // 1. 회원 2명 가입 및 경기 생성
        MemberResponse.MemberInfoResponse member1 = memberService.signup(new MemberRequest.SignUpRequest("user1@test.com", "1234", "유저1"));
        MemberResponse.MemberInfoResponse member2 = memberService.signup(new MemberRequest.SignUpRequest("user2@test.com", "1234", "유저2"));
        GameResponse.GameInfoResponse gameInfo = gameService.createGame(new GameRequest.CreateGameRequest("서울 결승전", LocalDateTime.now(), "잠실체육관"));

        // 공통 타겟 좌석 1개 확보
        Long targetSeatId = seatService.getSeatsByGameId(gameInfo.id()).get(0).id();

        // 2. 유저1이 먼저 타겟 좌석 예매 완료
        reservationService.reserve(new ReservationRequest.CreateReservationRequest(targetSeatId), "user1@test.com");

        // 3. 유저2가 동일한 타겟 좌석 예매 시도 -> 예외 무조건 발생
        assertThatThrownBy(() -> reservationService.reserve(new ReservationRequest.CreateReservationRequest(targetSeatId), "user2@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 예매된 좌석입니다.");
    }

    @Test
    @DisplayName("동시성 테스트 : 10명이 동시에 같은 좌석을 예매하면 1명만 성공해야함")
    void testConcurrentReservation() throws InterruptedException {
        // 1. 사전 데이터 준비
        memberService.signup(new MemberRequest.SignUpRequest("concurrent@test.com", "1234", "유저11"));
        GameResponse.GameInfoResponse gameInfo = gameService.createGame(new GameRequest.CreateGameRequest("동시성 경기 테스트", LocalDateTime.now().plusDays(1), "잠실체육관"));
        Long targetSeatId = seatService.getSeatsByGameId(gameInfo.id()).get(0).id();

        // 2. 10개의 스레드가 동시에 같은 좌석을 예매하도록 설정
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);  // 1번만 카운트 다운하면 전체 시작
        CountDownLatch doneLatch = new CountDownLatch(threadCount); // 완료 대기

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(()->{
                try {
                    startLatch.await();  // 모든 스레드 준비될 때까지 대기 (startLatch가 0이될 때까지)
                    reservationService.reserve(
                            new ReservationRequest.CreateReservationRequest(targetSeatId),
                            "concurrent@test.com"
                    );
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    failCount.incrementAndGet(); // 이미 예매된 좌석 예외
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        startLatch.countDown();  // 모든 스레드가 동시 출발
        doneLatch.await();
        executor.shutdown();

        // 3. 검증 (1건만 성공)
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(9);
    }

    @Test
    @Transactional
    @DisplayName("좌석 등급 검증 테스트 : 경기 생성 시 좌석 등급과 가격이 의도대로 배분되는지 확인")
    void testSeatGradingAndPricing() {
        // 1. 경기 생성
        GameRequest.CreateGameRequest gameRequest = new GameRequest.CreateGameRequest("등급 테스트 경기", LocalDateTime.now().plusDays(3), "잠실체육관");
        GameResponse.GameInfoResponse gameInfo = gameService.createGame(gameRequest);

        // 2. 좌석 조회
        List<SeatResponse.SeatInfoResponse> seats = seatService.getSeatsByGameId(gameInfo.id());

        // 3. 총 좌석 수 확인
        assertThat(seats).hasSize(50);

        // 4. 구간별 등급 및 가격 검증
        // 1~10번 : VIP석 (50000원)
        assertThat(seats.get(0).grade()).isEqualTo(SeatGrade.VIP);
        assertThat(seats.get(9).price()).isEqualTo(50000);

        // 11~20번 : R석 (30000원)
        assertThat(seats.get(10).grade()).isEqualTo(SeatGrade.R);
        assertThat(seats.get(19).price()).isEqualTo(30000);

        // 21~35번 : S석 (20000원)
        assertThat(seats.get(20).grade()).isEqualTo(SeatGrade.S);
        assertThat(seats.get(34).price()).isEqualTo(20000);

        // 36~50번 : 일반석 (50000원)
        assertThat(seats.get(35).grade()).isEqualTo(SeatGrade.GENERAL);
        assertThat(seats.get(49).price()).isEqualTo(10000);

    }
}
