package com.scoutvelocity.scoutvelocity.domain.player;

import com.scoutvelocity.scoutvelocity.domain.club.Club;
import com.scoutvelocity.scoutvelocity.domain.club.ClubInfo;
import com.scoutvelocity.scoutvelocity.domain.nationality.Nationality;
import com.scoutvelocity.scoutvelocity.domain.nationality.NationalityInfo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 선수 엔티티 (Step 2: Index)
 * 
 * Step 2: 인덱스 추가 + 쿼리 메서드
 * - DB 레벨 필터링으로 성능 개선
 * - 하지만 메서드 폭발 & N+1 문제 존재
 */
@Entity
@Table(name = "players", indexes = {
    @Index(name = "idx_club_id", columnList = "club_id"),
    @Index(name = "idx_nationality_id", columnList = "nationality_id"),
    @Index(name = "idx_player_positions", columnList = "player_positions"),
    @Index(name = "idx_age", columnList = "age"),
    @Index(name = "idx_potential", columnList = "potential"),
    @Index(name = "idx_overall", columnList = "overall")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Player {
    
    @Id
    @Column(name = "player_id", length = 50)
    private String id;
    
    // 기본 정보
    @Column(name = "short_name", length = 100)
    private String shortName;
    
    @Column(name = "long_name", length = 200)
    private String longName;
    
    @Column(name = "player_positions", length = 50)
    private String playerPositions;
    
    @Column(name = "overall")
    private Integer overall;
    
    @Column(name = "potential")
    private Integer potential;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "dob")
    private LocalDate dateOfBirth;
    
    // 금전 정보
    @Column(name = "value_eur", precision = 15, scale = 2)
    private BigDecimal valueEur;
    
    @Column(name = "wage_eur", precision = 15, scale = 2)
    private BigDecimal wageEur;
    
    @Column(name = "release_clause_eur", precision = 15, scale = 2)
    private BigDecimal releaseClauseEur;
    
    // Club 관계 (정규화) - Plain: FK 제약조건 없음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;
    
    @Embedded
    private ClubInfo clubInfo;
    
    // Nationality 관계 (정규화)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;
    
    @Embedded
    private NationalityInfo nationalityInfo;
    
    // 신체 정보
    @Embedded
    private PhysicalInfo physical;
    
    // 능력치
    @Embedded
    private PlayerStats stats;
    
    // FIFA 메타데이터
    @Column(name = "fifa_version")
    private Integer fifaVersion;
    
    @Column(name = "fifa_update")
    private Integer fifaUpdate;
    
    @Column(name = "update_as_of")
    private LocalDate updateAsOf;
    
    @Column(name = "real_face", length = 10)
    private String realFace; // Yes, No
    
    @Column(name = "player_tags", columnDefinition = "TEXT")
    private String playerTags; // #Speedster, #Dribbler, etc.
    
    @Column(name = "player_traits", columnDefinition = "TEXT")
    private String playerTraits; // Quick Step +, Rapid, Flair, etc.
    
    // 비즈니스 로직
    public boolean isWorldClass() {
        return overall >= 85;
    }
    
    public boolean isYoungTalent() {
        return age <= 23 && potential >= 85;
    }
    
    public boolean isVeteran() {
        return age >= 32;
    }
    
    public boolean hasClub() {
        return club != null;
    }
    
    public String getClubName() {
        return club != null ? club.getName() : "Free Agent";
    }
    
    public String getNationalityName() {
        return nationality.getName();
    }
    
    public boolean isExpensive() {
        return valueEur != null && valueEur.compareTo(BigDecimal.valueOf(50_000_000)) > 0;
    }
}
