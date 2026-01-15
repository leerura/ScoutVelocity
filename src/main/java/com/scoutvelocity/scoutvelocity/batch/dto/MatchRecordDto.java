package com.scoutvelocity.scoutvelocity.batch.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * MatchRecord CSV DTO
 */
@Data
public class MatchRecordDto {
    private String playerId;  // FK
    private Long recordId;    // PK
    private LocalDate matchDate;
    private Integer goals;
    private Integer assists;
    private Integer passTry;
    private Integer passSuccess;
    private Double rating;
}
