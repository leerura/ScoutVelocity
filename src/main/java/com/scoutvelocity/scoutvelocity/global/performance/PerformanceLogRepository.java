package com.scoutvelocity.scoutvelocity.global.performance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * PerformanceLog Repository
 */
public interface PerformanceLogRepository extends JpaRepository<PerformanceLog, Long> {
    
    /**
     * Step별 로그 조회 (N+1 방지)
     */
    List<PerformanceLog> findByStep(PerformanceStep step);
    
    /**
     * Step별 평균 실행 시간 조회
     */
    @Query("SELECT p.step, p.layer, AVG(p.durationMs) " +
           "FROM PerformanceLog p " +
           "WHERE p.step = :step " +
           "GROUP BY p.step, p.layer")
    List<Object[]> findAverageByStep(@Param("step") PerformanceStep step);
    
    /**
     * Step별 평균 쿼리 개수 조회
     */
    @Query("SELECT AVG(p.queryCount) FROM PerformanceLog p " +
           "WHERE p.step = :step AND p.layer = 'REPOSITORY'")
    Double findAverageQueryCountByStep(@Param("step") PerformanceStep step);
    
    /**
     * Step별 전체 데이터 삭제 (재측정용)
     */
    @Modifying
    void deleteByStep(PerformanceStep step);
    
    /**
     * 특정 Step의 측정 횟수
     */
    long countByStep(PerformanceStep step);
}
