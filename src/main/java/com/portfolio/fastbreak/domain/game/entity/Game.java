package com.portfolio.fastbreak.domain.game.entity;

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
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;   // 경기 제목

    @Column(nullable = false)
    private LocalDateTime gameDateTime;     // 경기 일시

    @Column(nullable = false)
    private String location;    // 경기 장소

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Game(String title, LocalDateTime gameDateTime, String location) {
        this.title = title;
        this.gameDateTime = gameDateTime;
        this.location = location;
    }
}
