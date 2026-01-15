package com.scoutvelocity.scoutvelocity.domain.player.dto;

import com.scoutvelocity.scoutvelocity.domain.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentFormResponseDto {
    private String playerId;
    private String name;
    private String clubName;
    private int matchesPlayed;      // 최근 경기 수
    private double avgRating;       // 평균 평점
    private double avgGoals;        // 경기당 평균 골
    private double avgAssists;      // 경기당 평균 어시스트

    public static RecentFormResponseDto of(Player player, int matches, double totalRating, int totalGoals, int totalAssists) {
        return RecentFormResponseDto.builder()
                .playerId(player.getId())
                .name(player.getShortName())
                .clubName(player.getClub() != null ? player.getClub().getName() : "Free Agent")
                .matchesPlayed(matches)
                .avgRating(matches > 0 ? BigDecimal.valueOf(totalRating / matches).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0)
                .avgGoals(matches > 0 ? BigDecimal.valueOf((double) totalGoals / matches).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0)
                .avgAssists(matches > 0 ? BigDecimal.valueOf((double) totalAssists / matches).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0)
                .build();
    }
}