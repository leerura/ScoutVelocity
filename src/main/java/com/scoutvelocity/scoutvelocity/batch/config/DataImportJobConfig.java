package com.scoutvelocity.scoutvelocity.batch.config;

import com.scoutvelocity.scoutvelocity.batch.dto.*;
import com.scoutvelocity.scoutvelocity.batch.processor.*;
import com.scoutvelocity.scoutvelocity.batch.writer.JpaItemWriterFactory;
import com.scoutvelocity.scoutvelocity.domain.club.Club;
import com.scoutvelocity.scoutvelocity.domain.league.League;
import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import com.scoutvelocity.scoutvelocity.domain.nationality.Nationality;
import com.scoutvelocity.scoutvelocity.domain.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 데이터 임포트 Job 설정
 * 
 * 실행 순서:
 * 1. Nationality (독립)
 * 2. League (독립)
 * 3. Club (League FK)
 * 4. Player (Club FK, Nationality FK)
 * 5. MatchRecord (Player FK)
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataImportJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JpaItemWriterFactory writerFactory;
    
    // Readers
    private final FlatFileItemReader<NationalityDto> nationalityItemReader;
    private final FlatFileItemReader<LeagueDto> leagueItemReader;
    private final FlatFileItemReader<ClubDto> clubItemReader;
    private final FlatFileItemReader<PlayerDto> playerItemReader;
    private final FlatFileItemReader<MatchRecordDto> matchRecordItemReader;
    
    // Processors
    private final NationalityProcessor nationalityProcessor;
    private final LeagueProcessor leagueProcessor;
    private final ClubProcessor clubProcessor;
    private final PlayerProcessor playerProcessor;
    private final MatchRecordProcessor matchRecordProcessor;
    
    /**
     * 메인 Job: 데이터 임포트
     */
    @Bean
    public Job dataImportJob() {
        return new JobBuilder("dataImportJob", jobRepository)
                .start(nationalityStep())
                .next(leagueStep())
                .next(clubStep())
                .next(playerStep())
                .next(matchRecordStep())
                .build();
    }
    
    /**
     * Step 1: Nationality 임포트
     */
    @Bean
    public Step nationalityStep() {
        JpaItemWriter<Nationality> writer = writerFactory.create();
        
        return new StepBuilder("nationalityStep", jobRepository)
                .<NationalityDto, Nationality>chunk(BatchConfig.CHUNK_SIZE_NATIONALITY, transactionManager)
                .reader(nationalityItemReader)
                .processor(nationalityProcessor)
                .writer(writer)
                .build();
    }
    
    /**
     * Step 2: League 임포트
     */
    @Bean
    public Step leagueStep() {
        JpaItemWriter<League> writer = writerFactory.create();
        
        return new StepBuilder("leagueStep", jobRepository)
                .<LeagueDto, League>chunk(BatchConfig.CHUNK_SIZE_LEAGUE, transactionManager)
                .reader(leagueItemReader)
                .processor(leagueProcessor)
                .writer(writer)
                .build();
    }
    
    /**
     * Step 3: Club 임포트
     */
    @Bean
    public Step clubStep() {
        JpaItemWriter<Club> writer = writerFactory.create();
        
        return new StepBuilder("clubStep", jobRepository)
                .<ClubDto, Club>chunk(BatchConfig.CHUNK_SIZE_CLUB, transactionManager)
                .reader(clubItemReader)
                .processor(clubProcessor)
                .writer(writer)
                .build();
    }
    
    /**
     * Step 4: Player 임포트 (가장 복잡)
     */
    @Bean
    public Step playerStep() {
        JpaItemWriter<Player> writer = writerFactory.create();
        
        return new StepBuilder("playerStep", jobRepository)
                .<PlayerDto, Player>chunk(BatchConfig.CHUNK_SIZE_PLAYER, transactionManager)
                .reader(playerItemReader)
                .processor(playerProcessor)
                .writer(writer)
                .build();
    }
    
    /**
     * Step 5: MatchRecord 임포트 (가장 대용량 300만건)
     */
    @Bean
    public Step matchRecordStep() {
        JpaItemWriter<MatchRecord> writer = writerFactory.create();
        
        return new StepBuilder("matchRecordStep", jobRepository)
                .<MatchRecordDto, MatchRecord>chunk(BatchConfig.CHUNK_SIZE_MATCH_RECORD, transactionManager)
                .reader(matchRecordItemReader)
                .processor(matchRecordProcessor)
                .writer(writer)
                .build();
    }
}
