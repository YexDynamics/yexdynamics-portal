package com.yexdynamics.portal_indie.persistence.repository;

import com.yexdynamics.portal_indie.persistence.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    List<Leaderboard> findByGameIdOrderByScoreValueDesc(Long gameId);
}