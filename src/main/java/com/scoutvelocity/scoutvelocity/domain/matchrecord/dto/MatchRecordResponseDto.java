package com.scoutvelocity.scoutvelocity.domain.matchrecord.dto;

import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchRecordResponseDto {
    private Long id;
    private String matchDate; // LocalDate -> String 변환
    private Integer goals;
    private Integer assists;
    private BigDecimal rating;

    public static MatchRecordResponseDto from(MatchRecord entity) {
        return MatchRecordResponseDto.builder()
                .id(entity.getId())
                .matchDate(entity.getMatchDate().toString())
                .goals(entity.getGoals())
                .assists(entity.getAssists())
                .rating(entity.getRating())
                .build();
    }
}