package com.scoutvelocity.scoutvelocity.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

/**
 * 선수 능력치 통합 (Embeddable)
 * 
 * 설계 원칙:
 * - 87개 능력치를 의미있는 그룹으로 분류
 * - 그룹별 Embeddable로 구조화 (가독성 ↑)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlayerStats {
    
    // 종합 능력치 (6개)
    @Column(name = "pace")
    private Integer pace;
    
    @Column(name = "shooting")
    private Integer shooting;
    
    @Column(name = "passing")
    private Integer passing;
    
    @Column(name = "dribbling")
    private Integer dribbling;
    
    @Column(name = "defending")
    private Integer defending;
    
    @Column(name = "physic")
    private Integer physic;
    
    // 세부 능력치 (그룹별)
    @Embedded
    private AttackingStats attacking;
    
    @Embedded
    private SkillStats skill;
    
    @Embedded
    private MovementStats movement;
    
    @Embedded
    private PowerStats power;
    
    @Embedded
    private MentalityStats mentality;
    
    @Embedded
    private DefendingStats defendingStats;  // ← 필드명 변경 (defending → defendingStats)
    
    @Embedded
    private GoalkeepingStats goalkeeping;
    
    // 비즈니스 로직
    public boolean isWorldClass() {
        return pace >= 85 || shooting >= 85 || passing >= 85 || dribbling >= 85;
    }
    
    public boolean isStriker() {
        return shooting != null && shooting >= 80 && pace != null && pace >= 75;
    }
    
    public boolean isDefender() {
        return defending != null && defending >= 80;
    }
    
    public boolean isGoalkeeper() {
        return goalkeeping != null && goalkeeping.getDiving() != null && goalkeeping.getDiving() >= 60;
    }
}
