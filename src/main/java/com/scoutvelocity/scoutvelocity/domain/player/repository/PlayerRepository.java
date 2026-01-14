package com.scoutvelocity.scoutvelocity.domain.player.repository;

import com.scoutvelocity.scoutvelocity.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Player Repository (Plain Version)
 */
public interface PlayerRepository extends JpaRepository<Player, String> {
}
