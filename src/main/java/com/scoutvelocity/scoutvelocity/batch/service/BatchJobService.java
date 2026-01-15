package com.scoutvelocity.scoutvelocity.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {

    private final JobLauncher jobLauncher;
    private final Job dataImportJob;

    @Async // 이제 외부에서 호출되므로 정상 작동함
    public void executeJobAsync() {
        try {
            log.info("=== 비동기 배치 시작 ===");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(dataImportJob, jobParameters);

            log.info("=== 비동기 배치 완료 ===");
        } catch (Exception e) {
            log.error("배치 실패", e);
        }
    }
}
