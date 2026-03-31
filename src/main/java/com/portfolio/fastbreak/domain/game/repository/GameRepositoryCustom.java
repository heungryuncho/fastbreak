package com.portfolio.fastbreak.domain.game.repository;

import com.portfolio.fastbreak.domain.game.dto.GameRequest;
import com.portfolio.fastbreak.domain.game.entity.Game;

import java.util.List;

public interface GameRepositoryCustom {
    List<Game> searchGames(GameRequest.GameSearchRequest request);
}
