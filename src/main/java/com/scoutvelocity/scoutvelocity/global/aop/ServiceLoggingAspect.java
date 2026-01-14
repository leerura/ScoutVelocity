package com.scoutvelocity.scoutvelocity.global.aop;

import com.scoutvelocity.scoutvelocity.global.performance.Layer;
import com.scoutvelocity.scoutvelocity.global.performance.PerformanceCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Service Layer 성능 측정 AOP
 * 
 * 모든 @Service의 public 메서드 실행 시간 측정
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceLoggingAspect {
    
    private final PerformanceCollector performanceCollector;
    
    @Around("within(@org.springframework.stereotype.Service *) && " +
            "execution(public * *(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // 성능 기록
            performanceCollector.record(Layer.SERVICE, className, methodName, duration);
        }
    }
}
