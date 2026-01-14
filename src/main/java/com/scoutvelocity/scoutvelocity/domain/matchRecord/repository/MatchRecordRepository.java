package com.scoutvelocity.scoutvelocity.domain.matchRecord.repository;

import com.scoutvelocity.scoutvelocity.domain.matchRecord.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * MatchRecord Repository (Plain Version)
 */
public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {
}
