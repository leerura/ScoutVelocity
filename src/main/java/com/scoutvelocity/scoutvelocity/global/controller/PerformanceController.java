package com.scoutvelocity.scoutvelocity.global.controller;

import com.scoutvelocity.scoutvelocity.global.performance.*;
import com.scoutvelocity.scoutvelocity.global.response.ApiResponse;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 성능 측정 리포트 Controller
 */
@RestController
@RequestMapping("/api/v1/performance")
@RequiredArgsConstructor
public class PerformanceController {
    
    private final PerformanceLogRepository performanceLogRepository;
    private final PerformanceCollector performanceCollector;
    
    /**
     * 현재 Step 리포트
     * 
     * GET /api/v1/performance/report
     */
    @GetMapping("/report")
    public ApiResponse<StepReportDto> getCurrentStepReport() {
        PerformanceStep currentStep = performanceCollector.getCurrentStep();
        return ApiResponse.success(generateStepReport(currentStep));
    }
    
    /**
     * 특정 Step 상세 리포트
     * 
     * GET /api/v1/performance/details?step=PLAIN
     */
    @GetMapping("/details")
    public ApiResponse<StepReportDto> getStepDetails(@RequestParam PerformanceStep step) {
        return ApiResponse.success(generateStepReport(step));
    }
    
    /**
     * 전체 Step 비교 리포트
     * 
     * GET /api/v1/performance/comparison
     */
    @GetMapping("/comparison")
    public ApiResponse<ComparisonReportDto> getComparisonReport() {
        Map<PerformanceStep, StepReportDto> reports = new HashMap<>();
        
        for (PerformanceStep step : PerformanceStep.values()) {
            reports.put(step, generateStepReport(step));
        }
        
        return ApiResponse.success(new ComparisonReportDto(reports, calculateImprovements(reports)));
    }
    
    /**
     * Step별 데이터 삭제 (재측정용)
     * 
     * DELETE /api/v1/performance/clear?step=PLAIN
     */
    @DeleteMapping("/clear")
    @Transactional
    public ApiResponse<Void> clearStepData(@RequestParam PerformanceStep step) {
        performanceLogRepository.deleteByStep(step);
        return ApiResponse.success();
    }
    
    // === Private Methods ===
    
    private StepReportDto generateStepReport(PerformanceStep step) {
        // N+1 방지: findAll() + filter 대신 findByStep() 사용
        List<PerformanceLog> logs = performanceLogRepository.findByStep(step);
        
        if (logs.isEmpty()) {
            return StepReportDto.empty(step);
        }
        
        Map<Layer, List<PerformanceLog>> byLayer = logs.stream()
                .collect(Collectors.groupingBy(PerformanceLog::getLayer));
        
        return StepReportDto.builder()
                .step(step)
                .totalMeasurements(logs.size())
                .controllerAvg(calculateAverage(byLayer.get(Layer.CONTROLLER)))
                .serviceAvg(calculateAverage(byLayer.get(Layer.SERVICE)))
                .repositoryAvg(calculateAverage(byLayer.get(Layer.REPOSITORY)))
                .avgQueryCount(calculateAverageQueryCount(byLayer.get(Layer.REPOSITORY)))
                .build();
    }
    
    private Long calculateAverage(List<PerformanceLog> logs) {
        if (logs == null || logs.isEmpty()) return 0L;
        return (long) logs.stream()
                .mapToLong(PerformanceLog::getDurationMs)
                .average()
                .orElse(0.0);
    }
    
    private Double calculateAverageQueryCount(List<PerformanceLog> logs) {
        if (logs == null || logs.isEmpty()) return 0.0;
        return logs.stream()
                .filter(log -> log.getQueryCount() != null)
                .mapToInt(PerformanceLog::getQueryCount)
                .average()
                .orElse(0.0);
    }
    
    private Map<String, String> calculateImprovements(Map<PerformanceStep, StepReportDto> reports) {
        Map<String, String> improvements = new HashMap<>();
        
        // NPE 방어: 각 Step의 리포트 존재 확인
        StepReportDto plainReport = reports.get(PerformanceStep.PLAIN);
        StepReportDto indexReport = reports.get(PerformanceStep.INDEX);
        StepReportDto optimizedReport = reports.get(PerformanceStep.OPTIMIZED);
        StepReportDto cachedReport = reports.get(PerformanceStep.CACHED);
        
        if (plainReport == null || indexReport == null || 
            optimizedReport == null || cachedReport == null) {
            return improvements;  // 데이터 부족 시 빈 Map 반환
        }
        
        Long plainTime = plainReport.getControllerAvg();
        Long indexTime = indexReport.getControllerAvg();
        Long optimizedTime = optimizedReport.getControllerAvg();
        Long cachedTime = cachedReport.getControllerAvg();
        
        if (plainTime > 0 && indexTime > 0) {
            improvements.put("PLAIN_to_INDEX", String.format("%.1fx faster", (double) plainTime / indexTime));
        }
        if (indexTime > 0 && optimizedTime > 0) {
            improvements.put("INDEX_to_OPTIMIZED", String.format("%.1fx faster", (double) indexTime / optimizedTime));
        }
        if (optimizedTime > 0 && cachedTime > 0) {
            improvements.put("OPTIMIZED_to_CACHED", String.format("%.1fx faster", (double) optimizedTime / cachedTime));
        }
        if (plainTime > 0 && cachedTime > 0) {
            improvements.put("PLAIN_to_CACHED", String.format("%.1fx faster", (double) plainTime / cachedTime));
        }
        
        return improvements;
    }
    
    // === DTOs ===
    
    @Getter
    @AllArgsConstructor
    @Builder
    static class StepReportDto {
        private PerformanceStep step;
        private Integer totalMeasurements;
        private Long controllerAvg;
        private Long serviceAvg;
        private Long repositoryAvg;
        private Double avgQueryCount;
        
        static StepReportDto empty(PerformanceStep step) {
            return StepReportDto.builder()
                    .step(step)
                    .totalMeasurements(0)
                    .controllerAvg(0L)
                    .serviceAvg(0L)
                    .repositoryAvg(0L)
                    .avgQueryCount(0.0)
                    .build();
        }
    }
    
    @Getter
    @AllArgsConstructor
    static class ComparisonReportDto {
        private Map<PerformanceStep, StepReportDto> reports;
        private Map<String, String> improvements;
    }
}
