package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 파워 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PowerStats {
    
    @Column(name = "power_shot_power")
    private Integer shotPower;
    
    @Column(name = "power_jumping")
    private Integer jumping;
    
    @Column(name = "power_stamina")
    private Integer stamina;
    
    @Column(name = "power_strength")
    private Integer strength;
    
    @Column(name = "power_long_shots")
    private Integer longShots;
}
