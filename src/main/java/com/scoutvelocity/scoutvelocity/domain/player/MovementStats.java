package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 이동 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MovementStats {
    
    @Column(name = "movement_acceleration")
    private Integer acceleration;
    
    @Column(name = "movement_sprint_speed")
    private Integer sprintSpeed;
    
    @Column(name = "movement_agility")
    private Integer agility;
    
    @Column(name = "movement_reactions")
    private Integer reactions;
    
    @Column(name = "movement_balance")
    private Integer balance;
}
