package com.scoutvelocity.scoutvelocity.batch.mapper;

import com.scoutvelocity.scoutvelocity.batch.dto.MatchRecordDto;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * FieldSetMapper for MatchRecordDto
 * 
 * Handles:
 * - String → LocalDate conversion for matchDate field
 * - Null-safe type conversions for all fields
 */
public class MatchRecordFieldSetMapper extends AbstractFieldSetMapper<MatchRecordDto> {

    @Override
    public MatchRecordDto mapFieldSet(FieldSet fs) throws BindException {
        MatchRecordDto dto = new MatchRecordDto();
        
        try {
            dto.setPlayerId(readString(fs, "playerId"));
            dto.setRecordId(readLong(fs, "recordId"));
            dto.setMatchDate(readLocalDate(fs, "matchDate"));  // CRITICAL: String → LocalDate
            dto.setGoals(readInteger(fs, "goals"));
            dto.setAssists(readInteger(fs, "assists"));
            dto.setPassTry(readInteger(fs, "passTry"));
            dto.setPassSuccess(readInteger(fs, "passSuccess"));
            dto.setRating(readDouble(fs, "rating"));
            
            return dto;
        } catch (Exception e) {
            throw new BindException(dto, "target");
        }
    }
}
