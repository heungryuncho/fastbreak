package com.portfolio.fastbreak.domain.reservation.entity;

import com.portfolio.fastbreak.domain.member.entity.Member;
import com.portfolio.fastbreak.domain.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime reservedAt;

    @Builder
    public Reservation(Member member, Seat seat, ReservationStatus status) {
        this.member = member;
        this.seat = seat;
        this.status = status;
    }

    // 결제 완료 처리
    public void complete() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태의 예약만 완료할 수 있습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
    }

    // 예매 취소 및 미결제 만료
    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        // 예약 취소 시 연결된 좌석 상태도 롤백
        this.seat.cancel();
    }
}
