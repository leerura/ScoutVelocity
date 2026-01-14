package com.scoutvelocity.scoutvelocity.domain.club;

import com.scoutvelocity.scoutvelocity.domain.league.League;
import jakarta.persistence.*;
import lombok.*;

/**
 * 클럽 엔티티 (Plain Version)
 * 
 * Step 1: Plain 상태 (인덱스X, 캐시X)
 */
@Entity
@Table(name = "clubs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Club {
    
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private League league;
    
    // 비즈니스 로직
    public boolean isTopLeague() {
        return league.isTopTier();
    }
    
    public String getLeagueName() {
        return league.getName();
    }
}
