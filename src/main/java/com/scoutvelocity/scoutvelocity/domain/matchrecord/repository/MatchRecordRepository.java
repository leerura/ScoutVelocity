package com.scoutvelocity.scoutvelocity.domain.matchrecord.repository;

import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * MatchRecord Repository (Plain Version)
 */
public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {
}
