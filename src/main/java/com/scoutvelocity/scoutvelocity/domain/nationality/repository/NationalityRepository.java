package com.scoutvelocity.scoutvelocity.domain.nationality.repository;

import com.scoutvelocity.scoutvelocity.domain.nationality.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Nationality Repository (Plain Version)
 */
public interface NationalityRepository extends JpaRepository<Nationality, Long> {
}
