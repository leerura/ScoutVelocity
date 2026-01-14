package com.scoutvelocity.scoutvelocity.global.performance;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 성능 측정 로그 Entity
 * 
 * 각 요청마다 Controller, Service, Repository 계층별 실행 시간과
 * 쿼리 개수를 기록하여 단계별 성능 비교 데이터 제공
 */
@Entity
@Table(name = "performance_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PerformanceLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Step 구분 (PLAIN, INDEX, OPTIMIZED, CACHED)
    @Column(name = "step", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PerformanceStep step;
    
    // Layer 구분 (CONTROLLER, SERVICE, REPOSITORY)
    @Column(name = "layer", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Layer layer;
    
    // 클래스명 (PlayerController, PlayerService, PlayerRepository)
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;
    
    // 메서드명 (getPlayersByClub, findByClub)
    @Column(name = "method_name", nullable = false, length = 100)
    private String methodName;
    
    // 실행 시간 (밀리초)
    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;
    
    // 실행된 쿼리 개수 (Hibernate Statistics)
    @Column(name = "query_count")
    private Integer queryCount;
    
    // 측정 시각
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
    
    // 요청 식별자 (같은 요청의 계층별 로그 그룹화)
    @Column(name = "request_id", length = 36)
    private String requestId;
}
