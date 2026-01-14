package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 선수 신체 정보 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PhysicalInfo {
    
    @Column(name = "height_cm")
    private Integer heightCm;
    
    @Column(name = "weight_kg")
    private Integer weightKg;
    
    @Column(name = "body_type", length = 50)
    private String bodyType; // Unique, Lean (185+), Normal (170-185), etc.
    
    @Column(name = "preferred_foot", length = 10)
    private String preferredFoot; // Right, Left
    
    @Column(name = "weak_foot")
    private Integer weakFoot; // 1~5
    
    @Column(name = "skill_moves")
    private Integer skillMoves; // 1~5
    
    @Column(name = "international_reputation")
    private Integer internationalReputation; // 1~5
    
    @Column(name = "work_rate", length = 30)
    private String workRate; // High/High, Medium/High, etc.
    
    // 비즈니스 로직
    public boolean isTall() {
        return heightCm != null && heightCm >= 185;
    }
    
    public boolean isShort() {
        return heightCm != null && heightCm <= 175;
    }
    
    public boolean isFiveStar() {
        return skillMoves != null && skillMoves == 5;
    }
}
