package com.scoutvelocity.scoutvelocity.batch.processor;

import com.scoutvelocity.scoutvelocity.batch.dto.MatchRecordDto;
import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import com.scoutvelocity.scoutvelocity.domain.player.Player;
import com.scoutvelocity.scoutvelocity.domain.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MatchRecordProcessor implements ItemProcessor<MatchRecordDto, MatchRecord> {
    
    private final PlayerRepository playerRepository;
    
    @Override
    public MatchRecord process(MatchRecordDto dto) throws Exception {
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + dto.getPlayerId()));
        
        return MatchRecord.builder()
                .id(dto.getRecordId())
                .player(player)
                .matchDate(dto.getMatchDate())
                .goals(dto.getGoals())
                .assists(dto.getAssists())
                .passTry(dto.getPassTry())
                .passSuccess(dto.getPassSuccess())
                .rating(dto.getRating() != null ? BigDecimal.valueOf(dto.getRating()) : null)
                .build();
    }
}
