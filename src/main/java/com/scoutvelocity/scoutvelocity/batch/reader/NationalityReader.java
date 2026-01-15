package com.scoutvelocity.scoutvelocity.batch.reader;
import com.scoutvelocity.scoutvelocity.batch.config.BatchConfig;
import com.scoutvelocity.scoutvelocity.batch.dto.NationalityDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
@Configuration
public class NationalityReader {
    @Bean
    public FlatFileItemReader<NationalityDto> nationalityItemReader() {
        return new FlatFileItemReaderBuilder<NationalityDto>()
                .name("nationalityItemReader")
                .resource(new FileSystemResource(BatchConfig.CSV_PATH + "nationalities.csv"))
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .names("id", "name")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(NationalityDto.class);
                }})
                .build();
    }
}
