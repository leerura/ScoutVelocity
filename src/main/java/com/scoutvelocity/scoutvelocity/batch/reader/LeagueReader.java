package com.scoutvelocity.scoutvelocity.batch.reader;
import com.scoutvelocity.scoutvelocity.batch.config.BatchConfig;
import com.scoutvelocity.scoutvelocity.batch.dto.LeagueDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
@Configuration
public class LeagueReader {
    @Bean
    public FlatFileItemReader<LeagueDto> leagueItemReader() {
        return new FlatFileItemReaderBuilder<LeagueDto>()
                .name("leagueItemReader")
                .resource(new FileSystemResource(BatchConfig.CSV_PATH + "leagues.csv"))
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .names("id", "name", "level")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(LeagueDto.class);
                }})
                .build();
    }
}
