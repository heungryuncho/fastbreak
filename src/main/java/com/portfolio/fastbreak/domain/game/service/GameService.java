package com.portfolio.fastbreak.domain.game.service;

import com.portfolio.fastbreak.domain.game.dto.GameRequest;
import com.portfolio.fastbreak.domain.game.dto.GameResponse;
import com.portfolio.fastbreak.domain.game.entity.Game;
import com.portfolio.fastbreak.domain.game.repository.GameRepository;
import com.portfolio.fastbreak.domain.seat.entity.Seat;
import com.portfolio.fastbreak.domain.seat.entity.SeatGrade;
import com.portfolio.fastbreak.domain.seat.entity.SeatStatus;
import com.portfolio.fastbreak.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SeatRepository seatRepository;

    // 1. 경기 등록
    @Transactional
    public GameResponse.GameInfoResponse createGame(GameRequest.CreateGameRequest request) {
        // 1. 요청 데이터를 바탕으로 Game 엔티티 생성
        Game game = Game.builder()
                .title(request.title())
                .gameDateTime(request.gameDatetime())
                .location(request.location())
                .build();

        // 2. DB에 저장
        Game savedGame = gameRepository.save(game);

        // 좌석 생성
        int totalSeats = (request.totalSeats() != null) ? request.totalSeats() : 50;
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= totalSeats; i++) {
            SeatGrade grade = determineGrade(i, totalSeats);

            // 등급별 가격 결정
            int price = getPriceForGrade(grade, request);

            Seat seat = Seat.builder()
                    .game(savedGame)
                    .seatNumber(i)
                    .grade(grade)
                    .price(price)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seats.add(seat);
        }
        seatRepository.saveAll(seats);

        return new GameResponse.GameInfoResponse(
                savedGame.getId(),
                savedGame.getTitle(),
                savedGame.getGameDateTime(),
                savedGame.getLocation()
        );
    }

    // 2. 전체 경기 목록 조회
    @Transactional(readOnly = true)
    public List<GameResponse.GameInfoResponse> getAllGames() {
        // 1. DB에서 모든 경기 목록을 가져옴
        List<Game> games = gameRepository.findAll();

        // 2. 조회된 Entity 리스트를 DTO로 변환하여 반환
        return games.stream()
                .map(game -> new GameResponse.GameInfoResponse(
                        game.getId(),
                        game.getTitle(),
                        game.getGameDateTime(),
                        game.getLocation()
                ))
                .collect(Collectors.toList());
    }

    // 등급별 가격 결정
    private int getPriceForGrade(SeatGrade grade, GameRequest.CreateGameRequest request) {
        return switch (grade) {
            case VIP -> (request.vipPrice() != null) ? request.vipPrice() : grade.getDefaultPrice();
            case R -> (request.rPrice() != null) ? request.rPrice() : grade.getDefaultPrice();
            case S -> (request.sPrice() != null) ? request.sPrice() : grade.getDefaultPrice();
            case GENERAL -> (request.generalPrice() != null) ? request.generalPrice() : grade.getDefaultPrice();
        };
    }

    // 좌석 번호와 전체 좌석 수에 따라 등급 판단
    private SeatGrade determineGrade(int seatNumber, int totalSeats) {
        double ratio = (double) seatNumber / totalSeats;

        if (ratio <= 0.1) return SeatGrade.VIP;
        if (ratio <= 0.3) return SeatGrade.R;
        if (ratio <= 0.6) return SeatGrade.S;
        return SeatGrade.GENERAL;
    }

    // 관리자
    @Transactional(readOnly = true)
    public GameResponse.GameStatsResponse getGameStats(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));

        List<Seat> seats = seatRepository.findByGameIdOrderBySeatNumberAsc(gameId);

        long totalSeats = seats.size();
        long reservedSeats = seats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.RESERVED)
                .count();

        long totalRevenue = seats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.RESERVED)
                .mapToLong(Seat::getPrice)
                .sum();

        double reservedRate = (totalSeats > 0) ? (double) reservedSeats / totalSeats * 100 : 0.0;

        return new GameResponse.GameStatsResponse(
                game.getId(),
                game.getTitle(),
                (int) totalSeats,
                reservedSeats,
                reservedRate,
                totalRevenue
        );
    }

    @Transactional(readOnly = true)
    public List<GameResponse.GameInfoResponse> searchGames(GameRequest.GameSearchRequest request) {
        List<Game> games = gameRepository.searchGames(request);
        return games.stream()
                .map(game -> new GameResponse.GameInfoResponse(
                        game.getId(),
                        game.getTitle(),
                        game.getGameDateTime(),
                        game.getLocation()
                ))
                .collect(Collectors.toList());
    }
}
