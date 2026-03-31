package com.portfolio.fastbreak.domain.seat.entity;

import lombok.Getter;

@Getter
public enum SeatGrade {
    VIP(50000),
    R(30000),
    S(20000),
    GENERAL(10000);

    private final int defaultPrice;

    SeatGrade(int defaultPrice) {
        this.defaultPrice = defaultPrice;
    }
}
