package com.scoutvelocity.scoutvelocity.domain.matchrecord.repository;

import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MatchRecord Repository (Step 2: Index + 쿼리 메서드)
 *
 * 시나리오 5~7용 쿼리 메서드
 */
public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    // 시나리오 5: 선수별 경기 기록 조회
    // 🚨 N+1 주의: player 정보 접근 시 추가 쿼리
    List<MatchRecord> findByPlayer_Id(String playerId);


    // 시나리오 7: 날짜 범위 조회
    List<MatchRecord> findByMatchDateBetween(LocalDate startDate, LocalDate endDate);

    // 선수 + 날짜 범위 조합
    List<MatchRecord> findByPlayer_IdAndMatchDateBetween(
            String playerId, LocalDate startDate, LocalDate endDate
    );
}