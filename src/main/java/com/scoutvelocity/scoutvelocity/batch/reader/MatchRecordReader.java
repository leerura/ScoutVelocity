package com.scoutvelocity.scoutvelocity.batch.reader;
import com.scoutvelocity.scoutvelocity.batch.config.BatchConfig;
import com.scoutvelocity.scoutvelocity.batch.dto.MatchRecordDto;
import com.scoutvelocity.scoutvelocity.batch.mapper.MatchRecordFieldSetMapper;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class MatchRecordReader {
    @Bean
    public FlatFileItemReader<MatchRecordDto> matchRecordItemReader() {
        return new FlatFileItemReaderBuilder<MatchRecordDto>()
                .name("matchRecordItemReader")
                .resource(new FileSystemResource(BatchConfig.CSV_PATH + "match_records_3m_uniform.csv"))
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .names("playerId","recordId","matchDate","goals","assists","passTry","passSuccess","rating")
                .fieldSetMapper(new MatchRecordFieldSetMapper())  // Custom mapper for LocalDate conversion
                .build();
    }
}