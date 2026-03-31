package com.portfolio.fastbreak.domain.seat.service;

import com.portfolio.fastbreak.domain.seat.dto.SeatResponse;
import com.portfolio.fastbreak.domain.seat.entity.Seat;
import com.portfolio.fastbreak.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public List<SeatResponse.SeatInfoResponse> getSeatsByGameId(Long gameId) {
        String cacheKey = "game:" + gameId + ":seats";

        // 1. Redis에서 먼저 데이터가 있는 지 확인
        List<SeatResponse.SeatInfoResponse> cachedSeats = (List<SeatResponse.SeatInfoResponse>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedSeats != null) {
            return cachedSeats; // 캐시가 있으면 즉시 반환
        }

        // 2. 캐시가 없으면 DB 조회
        List<Seat> seats = seatRepository.findByGameIdOrderBySeatNumberAsc(gameId);

        List<SeatResponse.SeatInfoResponse> responseList = seats.stream()
                .map(seat -> new SeatResponse.SeatInfoResponse(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getGrade(),
                        seat.getPrice(),
                        seat.getStatus()
                ))
                .collect(Collectors.toList());

        // 3. 조회 된 데이터를 10분간 Redis에 저장
        redisTemplate.opsForValue().set(cacheKey, responseList, Duration.ofMinutes(10));

        return responseList;
    }
}
