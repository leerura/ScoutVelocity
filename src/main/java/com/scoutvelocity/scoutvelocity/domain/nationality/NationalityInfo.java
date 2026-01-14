package com.scoutvelocity.scoutvelocity.domain.nationality;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 선수의 국가대표팀 정보 (Embeddable)
 * 
 * Player 엔티티에 Embedded로 사용됨
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NationalityInfo {
    
    @Column(name = "nation_team_id")
    private Long nationTeamId; // nullable (국가대표팀 미선발 가능)
    
    @Column(name = "nation_position", length = 20)
    private String position; // LW, ST, etc.
    
    @Column(name = "nation_jersey_number")
    private Integer jerseyNumber;
    
    // 비즈니스 로직
    public boolean isNationalTeamPlayer() {
        return nationTeamId != null;
    }
}
