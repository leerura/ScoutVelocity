package com.scoutvelocity.scoutvelocity.batch.reader;
import com.scoutvelocity.scoutvelocity.batch.config.BatchConfig;
import com.scoutvelocity.scoutvelocity.batch.dto.ClubDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
@Configuration
public class ClubReader {
    @Bean
    public FlatFileItemReader<ClubDto> clubItemReader() {
        return new FlatFileItemReaderBuilder<ClubDto>()
                .name("clubItemReader")
                .resource(new FileSystemResource(BatchConfig.CSV_PATH + "clubs.csv"))
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .names("id", "name", "leagueId")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(ClubDto.class);
                }})
                .build();
    }
}
