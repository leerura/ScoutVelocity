package com.scoutvelocity.scoutvelocity.domain.player.dto;

import com.scoutvelocity.scoutvelocity.domain.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopScorerResponseDto {
    private String playerId;
    private String name;
    private String clubName;
    private String position;
    private int totalGoals;
    private int totalAssists;
    private int totalPoints; // 공격 포인트 (골 + 어시스트)

    public static TopScorerResponseDto of(Player player, int goals, int assists) {
        return TopScorerResponseDto.builder()
                .playerId(player.getId())
                .name(player.getShortName())
                .clubName(player.getClub() != null ? player.getClub().getName() : "Free Agent")
                .position(player.getPlayerPositions())
                .totalGoals(goals)
                .totalAssists(assists)
                .totalPoints(goals + assists)
                .build();
    }
}