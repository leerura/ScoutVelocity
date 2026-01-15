package com.scoutvelocity.scoutvelocity.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch 공통 설정
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    
    // CSV 파일 경로
    public static final String CSV_PATH = "/Users/ethan/Desktop/ScoutVelocity/";
    
    // Chunk Size
    public static final int CHUNK_SIZE_NATIONALITY = 100;
    public static final int CHUNK_SIZE_LEAGUE = 50;
    public static final int CHUNK_SIZE_CLUB = 100;
    public static final int CHUNK_SIZE_PLAYER = 1000;
    public static final int CHUNK_SIZE_MATCH_RECORD = 5000;
}
