package com.portfolio.fastbreak.domain.game.dto;

import java.time.LocalDateTime;

public class GameRequest {
    public record CreateGameRequest(
            String title,
            LocalDateTime gameDatetime,
            String location,
            Integer totalSeats,
            Integer vipPrice,
            Integer rPrice,
            Integer sPrice,
            Integer generalPrice
    ) {}
}
