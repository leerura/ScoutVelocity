package com.scoutvelocity.scoutvelocity.batch.processor;

import com.scoutvelocity.scoutvelocity.batch.dto.NationalityDto;
import com.scoutvelocity.scoutvelocity.domain.nationality.Nationality;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class NationalityProcessor implements ItemProcessor<NationalityDto, Nationality> {
    
    @Override
    public Nationality process(NationalityDto dto) throws Exception {
        return Nationality.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
