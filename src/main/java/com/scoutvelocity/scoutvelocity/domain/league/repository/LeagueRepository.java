package com.scoutvelocity.scoutvelocity.domain.league.repository;

import com.scoutvelocity.scoutvelocity.domain.league.League;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * League Repository (Plain Version)
 */
public interface LeagueRepository extends JpaRepository<League, Long> {
}
