package com.scoutvelocity.scoutvelocity.domain.club;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

/**
 * 선수의 클럽 소속 정보 (Embeddable)
 * 
 * Player 엔티티에 Embedded로 사용됨
 * Club 엔티티와는 별개 (정규화 vs 비정규화 트레이드오프)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClubInfo {
    
    @Column(name = "club_position", length = 20)
    private String position; // LW, ST, SUB, RES, etc.
    
    @Column(name = "club_jersey_number")
    private Integer jerseyNumber;
    
    @Column(name = "club_loaned_from", length = 100)
    private String loanedFrom; // 임대 출신 클럽 (nullable)
    
    @Column(name = "club_joined_date")
    private LocalDate joinedDate;
    
    @Column(name = "club_contract_valid_until_year")
    private Integer contractValidUntilYear; // 2024, 2025, etc.
    
    // 비즈니스 로직
    public boolean isStarting() {
        return position != null && !position.equals("SUB") && !position.equals("RES");
    }
    
    public boolean isOnLoan() {
        return loanedFrom != null && !loanedFrom.isEmpty();
    }
    
    public boolean isContractExpiring(int currentYear) {
        return contractValidUntilYear != null && contractValidUntilYear <= currentYear;
    }
}
