package com.portfolio.fastbreak.domain.seat.entity;

import com.portfolio.fastbreak.domain.game.entity.Game;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 개의 좌석이 하나의 경기에 속함 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private Integer seatNumber; // 좌석 번호

    @Column(nullable = false)
    private Integer price;  // 좌석 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Builder
    public Seat(Game game, Integer seatNumber, Integer price, SeatStatus status) {
        this.game = game;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
    }

    // 예매 시 상태 변경
    public void reserve() {
        if (this.status == SeatStatus.RESERVED) {
            throw new IllegalStateException("이미 예매된 좌석입니다.");
        }
        this.status = SeatStatus.RESERVED;
    }

    // 예매 취소 시 상태 복구
    public void cancel() {
        this.status = SeatStatus.AVAILABLE;
    }
}
