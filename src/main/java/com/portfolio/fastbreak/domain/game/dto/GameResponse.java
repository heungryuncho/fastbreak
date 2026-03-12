package com.portfolio.fastbreak.domain.game.dto;

import java.time.LocalDateTime;

public class GameResponse {
    public record GameInfoResponse(Long id, String title, LocalDateTime gameDatetime, String location) {
    }
}
