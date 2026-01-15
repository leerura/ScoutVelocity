package com.scoutvelocity.scoutvelocity.batch.processor;

import com.scoutvelocity.scoutvelocity.batch.dto.LeagueDto;
import com.scoutvelocity.scoutvelocity.domain.league.League;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class LeagueProcessor implements ItemProcessor<LeagueDto, League> {
    
    @Override
    public League process(LeagueDto dto) throws Exception {
        return League.builder()
                .id(dto.getId())
                .name(dto.getName())
                .level(dto.getLevel())
                .build();
    }
}
