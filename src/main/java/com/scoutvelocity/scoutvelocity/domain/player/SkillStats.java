package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 기술 능력치 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SkillStats {
    
    @Column(name = "skill_dribbling")
    private Integer dribbling;
    
    @Column(name = "skill_curve")
    private Integer curve;
    
    @Column(name = "skill_fk_accuracy")
    private Integer fkAccuracy;
    
    @Column(name = "skill_long_passing")
    private Integer longPassing;
    
    @Column(name = "skill_ball_control")
    private Integer ballControl;
}
