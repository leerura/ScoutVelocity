package com.scoutvelocity.scoutvelocity.batch.dto;

import lombok.Data;

/**
 * Club CSV DTO
 */
@Data
public class ClubDto {
    private Long id;
    private String name;
    private Long leagueId;  // FK
}
