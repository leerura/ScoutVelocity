package com.scoutvelocity.scoutvelocity.domain.player.repository;

import com.scoutvelocity.scoutvelocity.domain.player.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Player Repository (Step 2: Index + 쿼리 메서드)
 *
 * Step 2 특징:
 * - 커스텀 쿼리 메서드로 DB 필터링
 * - 인덱스 효과 발휘
 *
 * 문제점 (의도적):
 * 1. 메서드 폭발: 조건마다 메서드 추가 필요
 * 2. N+1 문제: 연관관계 조회 시 추가 쿼리 발생
 * 3. 동적 쿼리 불가: 선택적 파라미터 처리 어려움
 */
public interface PlayerRepository extends JpaRepository<Player, String> {

    // ====== 시나리오 1: 클럽별 조회 ======
    List<Player> findByClub_Id(Long clubId);

    // ====== 시나리오 2: 포지션 + 복합 능력치 ======
    // 메서드 폭발의 시작...
    List<Player> findByPlayerPositionsContaining(String position);

    List<Player> findByPlayerPositionsContainingAndStats_PaceGreaterThanEqual(
            String position, Integer minPace
    );

    List<Player> findByPlayerPositionsContainingAndStats_PaceGreaterThanEqualAndStats_ShootingGreaterThanEqual(
            String position, Integer minPace, Integer minShooting
    );

    // 🔥 극악의 메서드명: 4개 조건 조합
    List<Player> findByPlayerPositionsContainingAndStats_PaceGreaterThanEqualAndStats_ShootingGreaterThanEqualAndStats_PhysicGreaterThanEqual(
            String position, Integer minPace, Integer minShooting, Integer minPhysical
    );

    // ====== 시나리오 3: 연령대 + 포텐셜 ======
    List<Player> findByAgeBetween(Integer minAge, Integer maxAge);

    List<Player> findByAgeBetweenAndPotentialGreaterThanEqual(
            Integer minAge, Integer maxAge, Integer minPotential
    );

    // 정렬 추가되면 또 폭발...
    List<Player> findByAgeBetweenAndPotentialGreaterThanEqualOrderByPotentialDesc(
            Integer minAge, Integer maxAge, Integer minPotential
    );

    List<Player> findByAgeBetweenAndPotentialGreaterThanEqualOrderByOverallDesc(
            Integer minAge, Integer maxAge, Integer minPotential
    );

    // ====== 시나리오 4: 리그 + 국적 + 페이징 ======
    // 🚨 N+1 주의: club.league 접근 시 추가 쿼리 발생
    Page<Player> findByClub_League_IdAndNationality_Id(
            Long leagueId, Long nationalityId, Pageable pageable
    );

    Page<Player> findByClub_League_Id(Long leagueId, Pageable pageable);

    Page<Player> findByNationality_Id(Long nationalityId, Pageable pageable);

    // ====== 기타 필요한 메서드들 (끝도 없음) ======
    List<Player> findByOverallGreaterThanEqual(Integer minOverall);

    List<Player> findByPotentialGreaterThanEqual(Integer minPotential);

    // ... 계속 추가 필요 (메서드 폭발 🔥)
}