package com.scoutvelocity.scoutvelocity.domain.league;

import jakarta.persistence.*;
import lombok.*;

/**
 * 리그 엔티티 (Plain Version)
 * 
 * Step 1: Plain 상태 (인덱스X, 캐시X)
 * - 성능 벤치마크 baseline 측정용
 */
@Entity
@Table(name = "leagues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class League {
    
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "level")
    private Integer level;
    
    // 비즈니스 로직
    public boolean isTopTier() {
        return this.level == 1;
    }
    
    public boolean isSecondTier() {
        return this.level == 2;
    }
}
