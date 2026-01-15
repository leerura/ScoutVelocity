package com.scoutvelocity.scoutvelocity.batch.processor;

import com.scoutvelocity.scoutvelocity.batch.dto.ClubDto;
import com.scoutvelocity.scoutvelocity.domain.club.Club;
import com.scoutvelocity.scoutvelocity.domain.league.League;
import com.scoutvelocity.scoutvelocity.domain.league.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClubProcessor implements ItemProcessor<ClubDto, Club> {
    
    private final LeagueRepository leagueRepository;
    
    @Override
    public Club process(ClubDto dto) throws Exception {
        League league = leagueRepository.findById(dto.getLeagueId())
                .orElseThrow(() -> new IllegalArgumentException("League not found: " + dto.getLeagueId()));
        
        return Club.builder()
                .id(dto.getId())
                .name(dto.getName())
                .league(league)
                .build();
    }
}
