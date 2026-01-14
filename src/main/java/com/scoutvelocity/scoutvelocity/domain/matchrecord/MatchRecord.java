package com.scoutvelocity.scoutvelocity.domain.matchrecord;

import com.scoutvelocity.scoutvelocity.domain.player.Player;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 경기 기록 엔티티 (Plain Version)
 * 
 * Step 1: Plain 상태 (인덱스X, 캐시X)
 * - 단방향 연관관계 (MatchRecord → Player)
 * - Player는 MatchRecord 컬렉션을 갖지 않음
 * - Cascade, orphanRemoval 불필요 (독립적 생명주기)
 */
@Entity
@Table(name = "match_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MatchRecord {
    
    @Id
    @Column(name = "record_id")
    private Long id;  // CSV의 record_id 그대로 사용
    
    // 단방향 연관관계: MatchRecord → Player
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;
    
    @Column(name = "match_date")
    private LocalDate matchDate;
    
    @Column(name = "goals")
    private Integer goals;
    
    @Column(name = "assists")
    private Integer assists;
    
    @Column(name = "pass_try")
    private Integer passTry;
    
    @Column(name = "pass_success")
    private Integer passSuccess;
    
    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;  // 6.2, 10.0 등 (0.0 ~ 10.0)
    
    // === 비즈니스 로직 ===
    
    /**
     * 패스 성공률 계산
     * @return 패스 성공률 (0.0 ~ 100.0)
     */
    public double getPassSuccessRate() {
        if (passTry == null || passTry == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(passSuccess)
                .divide(BigDecimal.valueOf(passTry), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    /**
     * 우수한 경기력 여부 (평점 8.0 이상)
     */
    public boolean isExcellentPerformance() {
        return rating != null && rating.compareTo(BigDecimal.valueOf(8.0)) >= 0;
    }
    
    /**
     * 멀티 골 여부 (2골 이상)
     */
    public boolean isMultiGoal() {
        return goals != null && goals >= 2;
    }
    
    /**
     * 득점 기여 (골 + 어시스트)
     */
    public int getGoalContribution() {
        int g = goals != null ? goals : 0;
        int a = assists != null ? assists : 0;
        return g + a;
    }
    
    /**
     * MOM(Man of the Match) 후보 여부
     * - 평점 9.0 이상 또는
     * - 2골 이상 또는
     * - 득점 기여 3 이상
     */
    public boolean isMomCandidate() {
        return (rating != null && rating.compareTo(BigDecimal.valueOf(9.0)) >= 0)
                || isMultiGoal()
                || getGoalContribution() >= 3;
    }
    
    /**
     * 저조한 경기력 여부 (평점 6.0 미만)
     */
    public boolean isPoorPerformance() {
        return rating != null && rating.compareTo(BigDecimal.valueOf(6.0)) < 0;
    }
}
