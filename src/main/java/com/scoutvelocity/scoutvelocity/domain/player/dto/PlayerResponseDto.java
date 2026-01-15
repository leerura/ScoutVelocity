package com.scoutvelocity.scoutvelocity.domain.player.dto;

import com.scoutvelocity.scoutvelocity.domain.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 선수 응답 DTO (Step 1: Plain)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerResponseDto {

    private String id;
    private String name;
    private String longName;
    private Integer age;
    private String clubName;
    private String nationalityName;
    private String positions;
    private Integer overall;
    private Integer potential;
    private Integer pace;
    private Integer shooting;
    private Integer passing;
    private Integer dribbling;
    private Integer defending;
    private Integer physic;
    private BigDecimal valueEur;
    private BigDecimal wageEur;

    public static PlayerResponseDto from(Player player) {
        return PlayerResponseDto.builder()
                .id(player.getId())
                .name(player.getShortName())
                .longName(player.getLongName())
                .age(player.getAge())
                .clubName(player.hasClub() ? player.getClubName() : "Free Agent")
                .nationalityName(player.getNationalityName())
                .positions(player.getPlayerPositions())
                .overall(player.getOverall())
                .potential(player.getPotential())
                .pace(player.getStats().getPace())
                .shooting(player.getStats().getShooting())
                .passing(player.getStats().getPassing())
                .dribbling(player.getStats().getDribbling())
                .defending(player.getStats().getDefending())
                .physic(player.getStats().getPhysic())
                .valueEur(player.getValueEur())
                .wageEur(player.getWageEur())
                .build();
    }
}
