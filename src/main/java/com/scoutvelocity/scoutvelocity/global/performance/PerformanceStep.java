package com.scoutvelocity.scoutvelocity.global.performance;

/**
 * 성능 최적화 단계
 */
public enum PerformanceStep {
    PLAIN,              // Step 1: 최적화 없음
    INDEX,              // Step 2: 인덱스 적용
    OPTIMIZED,          // Step 3: 쿼리 최적화 (N+1 해결)
    CACHED              // Step 4: Redis 캐싱
}
