package com.scoutvelocity.scoutvelocity.batch.controller;

import com.scoutvelocity.scoutvelocity.batch.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final BatchJobService batchJobService;
    private final JobExplorer jobExplorer;
    
    /**
     * 데이터 임포트 Job 실행 (비동기)
     */
    @PostMapping("/import")
    public String runDataImport() {
        batchJobService.executeJobAsync();
        return "Data import job started! Check logs for progress. Expected time: ~22 minutes";
    }
    
    /**
     * 최근 Job 실행 상태 조회
     */
    @GetMapping("/status")
    public String getJobStatus() {
        try {
            List<JobExecution> executions = jobExplorer.getJobInstances("dataImportJob", 0, 10)
                    .stream()
                    .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                    .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))  // 최신순
                    .collect(Collectors.toList());
            
            if (executions.isEmpty()) {
                return "No job execution found. Job may not have started yet.";
            }
            
            JobExecution latest = executions.get(0);
            
            // Duration 계산
            String duration = "In Progress";
            if (latest.getEndTime() != null && latest.getStartTime() != null) {
                long seconds = java.time.Duration.between(
                    latest.getStartTime(),
                    latest.getEndTime()
                ).getSeconds();
                duration = seconds + " seconds";
            }
            
            return String.format(
                    "Status: %s | Start: %s | Duration: %s | Exit: %s",
                    latest.getStatus(),
                    latest.getStartTime(),
                    duration,
                    latest.getExitStatus().getExitCode()
            );
            
        } catch (Exception e) {
            log.error("Failed to get job status", e);
            return "Failed to get job status: " + e.getMessage();
        }
    }
}
