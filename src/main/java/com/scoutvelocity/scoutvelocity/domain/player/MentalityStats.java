package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 멘탈 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MentalityStats {
    
    @Column(name = "mentality_aggression")
    private Integer aggression;
    
    @Column(name = "mentality_interceptions")
    private Integer interceptions;
    
    @Column(name = "mentality_positioning")
    private Integer positioning;
    
    @Column(name = "mentality_vision")
    private Integer vision;
    
    @Column(name = "mentality_penalties")
    private Integer penalties;
    
    @Column(name = "mentality_composure")
    private Integer composure;
}
