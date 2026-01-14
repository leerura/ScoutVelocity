package com.scoutvelocity.scoutvelocity.global.aop;

import com.scoutvelocity.scoutvelocity.global.performance.Layer;
import com.scoutvelocity.scoutvelocity.global.performance.PerformanceCollector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

/**
 * Repository Layer 성능 측정 AOP
 * 
 * 모든 JpaRepository의 메서드 실행 시간 및 쿼리 개수 측정
 * Hibernate Statistics를 활용하여 실제 실행된 SQL 쿼리 개수 파악
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RepositoryLoggingAspect {
    
    private final PerformanceCollector performanceCollector;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Around("execution(* com.scoutvelocity.scoutvelocity.domain..repository.*.*(..)) && " +
            "!execution(* *.toString(..)) && " +
            "!execution(* *.equals(..)) && " +
            "!execution(* *.hashCode(..)) && " +
            "!within(com.scoutvelocity.scoutvelocity.global.performance.PerformanceLogRepository)")
    public Object logRepositoryExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        // Hibernate Statistics 가져오기
        Statistics statistics = getHibernateStatistics();
        
        // 쿼리 카운트 초기화
        long queryCountBefore = statistics.getQueryExecutionCount();
        
        long startTime = System.currentTimeMillis();
        
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // 실행된 쿼리 개수
            // NOTE: Hibernate Statistics는 SessionFactory 전역 통계이므로
            // 멀티스레드 환경에서는 다른 요청의 쿼리가 포함될 수 있습니다.
            // 따라서 이 값은 근사치로 간주해야 합니다.
            long queryCountAfter = statistics.getQueryExecutionCount();
            int queryCount = (int) (queryCountAfter - queryCountBefore);
            
            // 성능 기록 (쿼리 개수 포함)
            performanceCollector.record(Layer.REPOSITORY, className, methodName, duration, queryCount);
        }
    }
    
    /**
     * Hibernate Statistics 조회
     */
    private Statistics getHibernateStatistics() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        
        // Statistics 활성화 여부 확인
        if (!statistics.isStatisticsEnabled()) {
            log.warn("Hibernate Statistics are disabled. Query counts will be inaccurate. " +
                    "Please ensure 'hibernate.generate_statistics=true' is set in application.yml");
        }
        
        return statistics;
    }
}
