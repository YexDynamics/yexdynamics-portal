package com.yexdynamics.portal_indie.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "leaderboards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_game_player",
                        columnNames = {"game_id", "player_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "score_value", nullable = false)
    private Integer scoreValue;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;
}