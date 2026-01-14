package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 공격 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AttackingStats {
    
    @Column(name = "attacking_crossing")
    private Integer crossing;
    
    @Column(name = "attacking_finishing")
    private Integer finishing;
    
    @Column(name = "attacking_heading_accuracy")
    private Integer headingAccuracy;
    
    @Column(name = "attacking_short_passing")
    private Integer shortPassing;
    
    @Column(name = "attacking_volleys")
    private Integer volleys;
}
