package com.yexdynamics.portal_indie.persistence.repository;

import com.yexdynamics.portal_indie.persistence.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByTitle(String title);
}