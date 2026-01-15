package com.scoutvelocity.scoutvelocity.domain.player.service;

import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import com.scoutvelocity.scoutvelocity.domain.matchrecord.dto.MatchRecordResponseDto;
import com.scoutvelocity.scoutvelocity.domain.matchrecord.repository.MatchRecordRepository;
import com.scoutvelocity.scoutvelocity.domain.player.Player;
import com.scoutvelocity.scoutvelocity.domain.player.dto.PlayerResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.dto.RecentFormResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.dto.TopScorerResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.repository.PlayerRepository;
import com.scoutvelocity.scoutvelocity.global.response.PageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 선수 조회 서비스 (Step 2: Index + 쿼리 메서드 - 개선되지만 한계 명확)
 * 전략: 커스텀 쿼리 메서드로 DB 필터링 (일부는 여전히 Stream 의존)
 * 개선점:
 * - DB 레벨 필터링으로 메모리 부하 감소
 * - 인덱스 활용으로 조회 속도 향상
 * 한계점 (의도적):
 * 1. 메서드 폭발: 조건 조합마다 Repository 메서드 필요
 * 2. N+1 문제: 연관관계 로딩 시 추가 쿼리 발생
 * 3. 동적 쿼리 불가: 선택적 파라미터 처리 어려움
 * 4. 집계 쿼리: 여전히 메모리 집계 (시나리오 6, 7)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PlayerQueryService {

    private final PlayerRepository playerRepository;
    private final MatchRecordRepository matchRecordRepository;

    /**
     * 시나리오 1: 클럽별 선수 조회
     * <p>
     * ✅ 개선: findByClub_Id 사용 → DB 필터링
     * ✅ 효과: 100,000건 → ~30건만 로드
     * ⚠️ 한계: N+1 가능 (club 정보 접근 시)
     * 결과: 1초 정도 안 걸림
     */
    public List<PlayerResponseDto> findByClub(Long clubId) {
        log.info("[INDEX] 클럽별 조회 시작 - clubId: {}", clubId);

        // Step 1과 차이: findAll() → findByClub_Id()
        List<Player> players = playerRepository.findByClub_Id(clubId);
        log.info("[INDEX] DB 필터링 완료 - 조회 결과 {}건 (인덱스 활용)", players.size());

        List<PlayerResponseDto> result = players.stream()
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        log.info("[INDEX] 클럽별 조회 완료 - 결과 {}건", result.size());
        return result;
    }

    /**
     * 시나리오 2: 포지션 + 복합 능력치 필터링
     * ⚠️ 메서드 폭발 문제 발생!
     * - 조건 조합마다 Repository 메서드 필요
     * - 4개 조건 → 2^4 = 16개 메서드 필요
     * - 현실적으로 불가능 → 부분적 쿼리 메서드 + Stream 병행
     * ✅ 개선: position 필터링은 DB에서 처리
     * ❌ 한계: 나머지 능력치는 여전히 메모리 필터링
     * 결과: 1초 정도 걸림
     */
    public List<PlayerResponseDto> findByPositionAndStats(
            String position,
            Integer minPace,
            Integer minShooting,
            Integer minPhysical
    ) {
        log.info("[INDEX] 복합 필터링 조회 시작");

        // 포지션이 있으면 DB 필터링, 없으면 전체 조회
        List<Player> players;
        if (position != null) {
            // ✅ 개선: position은 DB에서 필터링 (인덱스 활용)
            players = playerRepository.findByPlayerPositionsContaining(position);
            log.info("[INDEX] 포지션 필터링 완료 - {}건 (DB 처리)", players.size());
        } else {
            // ⚠️ 한계: position이 null이면 여전히 findAll()
            players = playerRepository.findAll();
            log.warn("[INDEX] 포지션 조건 없음 - 전체 {}건 로드 (비효율)", players.size());
        }

        // ❌ 한계: 나머지 조건은 메모리 필터링 (메서드 폭발 회피 위해)
        List<PlayerResponseDto> result = players.stream()
                .filter(p -> minPace == null ||
                        (p.getStats().getPace() != null && p.getStats().getPace() >= minPace))
                .filter(p -> minShooting == null ||
                        (p.getStats().getShooting() != null && p.getStats().getShooting() >= minShooting))
                .filter(p -> minPhysical == null ||
                        (p.getStats().getPhysic() != null && p.getStats().getPhysic() >= minPhysical))
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        log.info("[INDEX] 복합 필터링 완료 - 결과 {}건 (Stream 후처리)", result.size());
        return result;
    }

    /**
     * 시나리오 3: 연령대 + 포텐셜 기반 유망주 발굴 (정렬 포함)
     * ⚠️ 메서드 폭발 + 동적 정렬 불가 문제
     * - age 범위 + potential 조건 → 쿼리 메서드로 처리 가능
     * - 하지만 정렬 기준이 동적 → 메서드 추가 필요
     * (OrderByPotentialDesc, OrderByOverallDesc, OrderByAgeDesc...)
     * ✅ 개선: age, potential 필터링은 DB 처리
     * ❌ 한계: 동적 정렬은 메모리에서 처리 (메서드 폭발 회피)
     * 1초 정도 걸림
     */
    public List<PlayerResponseDto> findYoungTalents(
            Integer minAge,
            Integer maxAge,
            Integer minPotential,
            String sortBy,
            String order
    ) {
        log.info("[INDEX] 유망주 발굴 시작");

        List<Player> players;

        // ✅ 개선: age, potential 조건은 DB 필터링
        if (minAge != null && maxAge != null && minPotential != null) {
            players = playerRepository.findByAgeBetweenAndPotentialGreaterThanEqual(
                    minAge, maxAge, minPotential
            );
            log.info("[INDEX] DB 필터링 완료 - {}건 (인덱스 활용)", players.size());
        } else if (minAge != null && maxAge != null) {
            players = playerRepository.findByAgeBetween(minAge, maxAge);
            log.info("[INDEX] 연령대 필터링 완료 - {}건", players.size());
        } else {
            // ⚠️ 조건 조합이 없으면 findAll() (동적 쿼리 불가의 한계)
            players = playerRepository.findAll();
            log.warn("[INDEX] 조건 불충분 - 전체 {}건 로드", players.size());

            // Stream으로 후처리
            players = players.stream()
                    .filter(p -> minAge == null || (p.getAge() != null && p.getAge() >= minAge))
                    .filter(p -> maxAge == null || (p.getAge() != null && p.getAge() <= maxAge))
                    .filter(p -> minPotential == null || (p.getPotential() != null && p.getPotential() >= minPotential))
                    .collect(Collectors.toList());
        }

        // ❌ 한계: 동적 정렬은 메모리 처리 (정렬마다 메서드 추가는 비현실적)
        List<PlayerResponseDto> result = players.stream()
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        Comparator<PlayerResponseDto> comparator = getComparator(sortBy != null ? sortBy : "overall");
        if ("asc".equalsIgnoreCase(order)) {
            result.sort(comparator);
        } else {
            result.sort(comparator.reversed());
        }

        log.info("[INDEX] 유망주 발굴 완료 - 결과 {}건 (정렬은 메모리)", result.size());
        return result;
    }

    /**
     * 시나리오 4: 리그 + 국적 + 페이징 (복합 조건)
     * ✅ 개선: DB 필터링 + Pageable 활용
     * ⚠️ N+1 문제: club.league 접근 시 추가 쿼리 발생 (fetch join 없음)
     * ❌ 한계: 조건 조합마다 메서드 필요 (3가지 조합 = 3개 메서드)
     * 결과: 1초 정도 걸림
     */
    public PageResponseDto<PlayerResponseDto> findByLeagueAndNationality(
            Long leagueId,
            Long nationalityId,
            int page,
            int size
    ) {
        log.info("[INDEX] 리그+국적 조회 시작");

        Pageable pageable = PageRequest.of(page, size);
        Page<Player> playerPage;

        // ❌ 한계: 조건 조합마다 다른 메서드 호출 (동적 쿼리 불가)
        if (leagueId != null && nationalityId != null) {
            // ⚠️ N+1 위험: club -> league 조인 없이 프록시로 접근
            playerPage = playerRepository.findByClub_League_IdAndNationality_Id(
                    leagueId, nationalityId, pageable
            );
            log.info("[INDEX] 리그+국적 필터링 완료 - {}건 (N+1 가능)", playerPage.getTotalElements());
        } else if (leagueId != null) {
            playerPage = playerRepository.findByClub_League_Id(leagueId, pageable);
            log.info("[INDEX] 리그 필터링 완료 - {}건", playerPage.getTotalElements());
        } else if (nationalityId != null) {
            playerPage = playerRepository.findByNationality_Id(nationalityId, pageable);
            log.info("[INDEX] 국적 필터링 완료 - {}건", playerPage.getTotalElements());
        } else {
            // 조건 없으면 전체 조회 (비효율)
            playerPage = playerRepository.findAll(pageable);
            log.warn("[INDEX] 조건 없음 - 전체 페이징 (비효율)");
        }

        Page<PlayerResponseDto> result = playerPage.map(PlayerResponseDto::from);

        log.info("[INDEX] 리그+국적 조회 완료 - 페이지 {}/{}", page + 1, result.getTotalPages());
        return PageResponseDto.of(result);
    }

    /**
     * 시나리오 5: 선수별 경기 기록 조회 (JOIN 개선)
     * ✅ 개선: findByPlayer_Id 사용 → 특정 선수 기록만 조회
     * ✅ 효과: 3,000,000건 → ~100건으로 대폭 감소
     * ⚠️ 한계: player 정보 접근 시 N+1 가능 (fetch join 없음)
     * 결과: 1초도 안 걸림
     */
    public List<MatchRecordResponseDto> findMatchRecordsByPlayer(String playerId) {
        log.info("[INDEX] 경기 기록 조회 시작 - playerId: {}", playerId);

        // ✅ 개선: 전체 로드 없이 DB에서 바로 필터링
        List<MatchRecord> records = matchRecordRepository.findByPlayer_Id(playerId);

        if (records.isEmpty()) {
            log.warn("[INDEX] 경기 기록 없음 - playerId: {}", playerId);
            throw new IllegalArgumentException("Player not found or no match records: " + playerId);
        }

        log.info("[INDEX] DB 필터링 완료 - {}건 (인덱스 활용)", records.size());

        List<MatchRecordResponseDto> result = records.stream()
                .map(MatchRecordResponseDto::from)
                .collect(Collectors.toList());

        log.info("[INDEX] 경기 기록 조회 완료 - 결과 {}건", result.size());
        return result;
    }

    /**
     * 시나리오 6: 골 + 어시스트 상위 공격수 조회 (메모리 집계)
     * ❌ 여전히 비효율: 메모리 집계 유지
     * 이유:
     * - GROUP BY, SUM, HAVING을 쿼리 메서드로 표현 불가
     * - @Query로 네이티브 SQL 작성해야 하지만 Step 2 단계에선 제외
     * - 집계 기능은 Step 3(QueryDSL)에서 해결 예정
     * ⚠️ 한계: 여전히 전체 경기 기록 로드 (3,000,000건)
     * 결과: 70초 정도 걸림
     */
    public List<TopScorerResponseDto> findTopScorers(
            String position,
            Integer minGoals,
            Integer minAssists,
            int limit
    ) {
        log.info("[INDEX] 상위 득점자 집계 시작 - position: {}, minGoals: {}, minAssists: {}",
                position, minGoals, minAssists);

        // ❌ 한계: 집계는 여전히 findAll() (쿼리 메서드로는 GROUP BY 불가)
        List<MatchRecord> allRecords = matchRecordRepository.findAll();
        log.warn("[INDEX] 전체 경기 기록 로드 - {}건 (집계는 여전히 비효율)", allRecords.size());

        // 메모리 집계 (Step 1과 동일)
        Map<Player, List<MatchRecord>> playerRecordsMap = allRecords.stream()
                .filter(r -> position == null ||
                        (r.getPlayer() != null && r.getPlayer().getPlayerPositions().contains(position)))
                .collect(Collectors.groupingBy(MatchRecord::getPlayer));

        List<TopScorerResponseDto> result = playerRecordsMap.entrySet().stream()
                .map(entry -> {
                    Player player = entry.getKey();
                    List<MatchRecord> records = entry.getValue();

                    int totalGoals = records.stream().mapToInt(MatchRecord::getGoals).sum();
                    int totalAssists = records.stream().mapToInt(MatchRecord::getAssists).sum();

                    return TopScorerResponseDto.of(player, totalGoals, totalAssists);
                })
                .filter(dto -> minGoals == null || dto.getTotalGoals() >= minGoals)
                .filter(dto -> minAssists == null || dto.getTotalAssists() >= minAssists)
                .sorted(Comparator.comparingInt(TopScorerResponseDto::getTotalPoints).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        log.info("[INDEX] 상위 득점자 집계 완료 - 결과 {}건 (여전히 메모리 처리)", result.size());
        return result;
    }

    /**
     * 시나리오 7: 최근 폼 상태 조회 (날짜 범위 + 집계)
     * ⚠️ 부분 개선: 날짜 범위는 DB 필터링 가능
     * ❌ 여전히 비효율: 집계는 메모리 처리
     * 개선점:
     * - findByMatchDateBetween으로 날짜 범위 DB 필터링
     * - 인덱스 활용으로 3,000,000건 → ~10,000건 감소
     * 한계점:
     * - GROUP BY, AVG 집계는 여전히 메모리
     * - 선수별 그룹핑 및 평균 계산 CPU 부하
     * 결과: 70초 정도
     */
    public List<RecentFormResponseDto> findRecentForm(
            LocalDate startDate,
            LocalDate endDate,
            int minMatches,
            int limit
    ) {
        log.info("[INDEX] 최근 폼 조회 시작 - {} ~ {}, minMatches: {}", startDate, endDate, minMatches);

        // ✅ 개선: 날짜 범위는 DB에서 필터링 (인덱스 활용)
        List<MatchRecord> records = matchRecordRepository.findByMatchDateBetween(startDate, endDate);
        log.info("[INDEX] 날짜 범위 필터링 완료 - {}건 (DB 처리, 인덱스 활용)", records.size());

        // ❌ 한계: 집계는 여전히 메모리 처리
        Map<Player, List<MatchRecord>> playerRecordsMap = records.stream()
                .filter(r -> r.getPlayer() != null)
                .collect(Collectors.groupingBy(MatchRecord::getPlayer));

        List<RecentFormResponseDto> result = playerRecordsMap.entrySet().stream()
                .map(entry -> {
                    Player player = entry.getKey();
                    List<MatchRecord> playerRecords = entry.getValue();

                    int matches = playerRecords.size();
                    if (matches < minMatches) return null;

                    int totalGoals = playerRecords.stream().mapToInt(MatchRecord::getGoals).sum();
                    int totalAssists = playerRecords.stream().mapToInt(MatchRecord::getAssists).sum();
                    double totalRating = playerRecords.stream()
                            .map(MatchRecord::getRating)
                            .filter(java.util.Objects::nonNull)
                            .mapToDouble(BigDecimal::doubleValue)
                            .sum();

                    return RecentFormResponseDto.of(player, matches, totalRating, totalGoals, totalAssists);
                })
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparingDouble(RecentFormResponseDto::getAvgRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        log.info("[INDEX] 최근 폼 조회 완료 - 결과 {}건 (집계는 메모리)", result.size());
        return result;
    }

    private Comparator<PlayerResponseDto> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "potential" -> Comparator.comparing(PlayerResponseDto::getPotential,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "age" -> Comparator.comparing(PlayerResponseDto::getAge,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "value" -> Comparator.comparing(PlayerResponseDto::getValueEur,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(PlayerResponseDto::getOverall,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }


}