package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 골키핑 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GoalkeepingStats {
    
    @Column(name = "goalkeeping_diving")
    private Integer diving;
    
    @Column(name = "goalkeeping_handling")
    private Integer handling;
    
    @Column(name = "goalkeeping_kicking")
    private Integer kicking;
    
    @Column(name = "goalkeeping_positioning")
    private Integer positioning;
    
    @Column(name = "goalkeeping_reflexes")
    private Integer reflexes;
    
    @Column(name = "goalkeeping_speed")
    private Integer speed;
}
