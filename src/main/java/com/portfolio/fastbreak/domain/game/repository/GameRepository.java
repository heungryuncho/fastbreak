package com.portfolio.fastbreak.domain.game.repository;

import com.portfolio.fastbreak.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {
}
