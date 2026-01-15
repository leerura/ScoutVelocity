package com.scoutvelocity.scoutvelocity.global.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 성능 측정 데이터 수집기
 * 
 * AOP에서 측정한 데이터를 DB에 저장
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceCollector {
    
    private final PerformanceLogRepository performanceLogRepository;
    
    // application.yml에서 현재 Step 주입
    @Value("${performance.current-step:PLAIN}")
    private String currentStepName;
    
    // 요청별 고유 ID (ThreadLocal)
    private final ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    /**
     * 성능 데이터 기록 (쿼리 개수 포함)
     * 중요: 부모 트랜잭션이 readOnly여도 로그는 저장되어야 하므로
     * 항상 새로운 트랜잭션을 생성(REQUIRES_NEW)해서 실행함.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Layer layer, String className, String methodName, long durationMs) {
        record(layer, className, methodName, durationMs, null);
    }
    
    /**
     * 성능 데이터 기록 (쿼리 개수 포함)
     * 중요: 부모 트랜잭션이 readOnly여도 로그는 저장되어야 하므로
     * 항상 새로운 트랜잭션을 생성(REQUIRES_NEW)해서 실행함.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Layer layer, String className, String methodName, long durationMs, Integer queryCount) {
        try {
            // 설정값 검증
            PerformanceStep step;
            try {
                step = PerformanceStep.valueOf(currentStepName);
            } catch (IllegalArgumentException e) {
                log.error("Invalid performance.current-step value: {}. Must be one of: PLAIN, INDEX, OPTIMIZED, CACHED", 
                         currentStepName);
                return;  // 잘못된 설정값이면 기록 중단
            }
            
            PerformanceLog performanceLog = PerformanceLog.builder()
                    .step(step)
                    .layer(layer)
                    .className(className)
                    .methodName(methodName)
                    .durationMs(durationMs)
                    .queryCount(queryCount)
                    .measuredAt(LocalDateTime.now())
                    .requestId(requestId.get())
                    .build();
            
            performanceLogRepository.save(performanceLog);
            
            log.info("[{}][{}] {}.{} executed in {}ms (queries: {})", 
                    step, layer, className, methodName, durationMs, queryCount);
            
        } catch (Exception e) {
            log.error("Failed to record performance log", e);
        }
    }
    
    /**
     * 새로운 요청 시작 (RequestId 초기화)
     */
    public void startRequest() {
        requestId.set(UUID.randomUUID().toString());
    }
    
    /**
     * 요청 종료 (ThreadLocal 정리)
     */
    public void endRequest() {
        requestId.remove();
    }
    
    /**
     * 현재 Step 조회
     */
    public PerformanceStep getCurrentStep() {
        return PerformanceStep.valueOf(currentStepName);
    }
}
