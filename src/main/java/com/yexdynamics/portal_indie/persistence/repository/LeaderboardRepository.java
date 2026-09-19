package com.yexdynamics.portal_indie.persistence.repository;

import com.yexdynamics.portal_indie.persistence.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    Optional<Leaderboard> findByGameIdAndPlayerId(Long gameId, Long playerId);

    List<Leaderboard> findByGameIdOrderByScoreValueDesc(Long gameId);
}