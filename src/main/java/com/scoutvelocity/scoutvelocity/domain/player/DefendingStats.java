package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 수비 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DefendingStats {
    
    @Column(name = "defending_marking_awareness")
    private Integer markingAwareness;
    
    @Column(name = "defending_standing_tackle")
    private Integer standingTackle;
    
    @Column(name = "defending_sliding_tackle")
    private Integer slidingTackle;
}
