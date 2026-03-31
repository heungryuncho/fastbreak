package com.portfolio.fastbreak.domain.reservation.entity;

public enum ReservationStatus {
    PENDING,        // 결제 대기 (5분)
    COMPLETED,      // 예매 / 결제 완료
    CANCELLED       // 취소 (사용자 취소 혹은 미결제 완료)
}
