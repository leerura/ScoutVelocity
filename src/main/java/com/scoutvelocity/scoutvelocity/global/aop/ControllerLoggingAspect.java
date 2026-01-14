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
 * Controller Layer 성능 측정 AOP
 * 
 * 모든 @RestController의 public 메서드 실행 시간 측정
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerLoggingAspect {
    
    private final PerformanceCollector performanceCollector;
    
    @Around("within(@org.springframework.web.bind.annotation.RestController *) && " +
            "execution(public * *(..)) && " +
            "!within(com.scoutvelocity.scoutvelocity.global.controller.PerformanceController)")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // 요청 시작 (RequestId 생성)
        performanceCollector.startRequest();
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // 성능 기록
            performanceCollector.record(Layer.CONTROLLER, className, methodName, duration);
            
            // 요청 종료
            performanceCollector.endRequest();
        }
    }
}
