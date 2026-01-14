package com.scoutvelocity.scoutvelocity.domain.nationality;

import jakarta.persistence.*;
import lombok.*;

/**
 * 국적 엔티티 (Plain Version)
 * 
 * Step 1: Plain 상태 (인덱스X, 캐시X)
 */
@Entity
@Table(name = "nationalities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Nationality {
    
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", length = 100)
    private String name;
}
