package com.yexdynamics.portal_indie.persistence.repository;

import com.yexdynamics.portal_indie.persistence.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByTitle(String title);

    List<Game> findAllByActiveTrue();

    Optional<Game> findByIdAndActiveTrue(Long id);

    Optional<Game> findByGameTokenAndActiveTrue(String gameToken);

    boolean existsByGameToken(String gameToken);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    List<Game> findAllByOwnerId(Long ownerId);

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.owner ORDER BY g.createdAt DESC")
    List<Game> findAllWithOwner();
}