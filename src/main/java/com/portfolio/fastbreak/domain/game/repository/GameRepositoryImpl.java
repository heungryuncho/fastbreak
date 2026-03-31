package com.portfolio.fastbreak.domain.game.repository;

import com.portfolio.fastbreak.domain.game.dto.GameRequest;
import com.portfolio.fastbreak.domain.game.entity.Game;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.portfolio.fastbreak.domain.game.entity.QGame.game;

@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Game> searchGames(GameRequest.GameSearchRequest request) {
        return queryFactory
                .selectFrom(game)
                .where(
                        titleContains(request.title()),
                        locationEq(request.location()),
                        gameTimeBetween(request.startDateTime(), request.endDateTime())
                )
                .fetch();
    }

    // 조건부 쿼리들 (Dynamic Query)
    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? game.title.contains(title) : null;
    }

    private BooleanExpression locationEq(String location) {
        return StringUtils.hasText(location) ? game.location.eq(location) : null;
    }

    private BooleanExpression gameTimeBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return null;
        if (start != null && end == null) return game.gameDateTime.goe(start);
        if (start == null && end != null) return game.gameDateTime.loe(end);
        return game.gameDateTime.between(start, end);
    }
}
