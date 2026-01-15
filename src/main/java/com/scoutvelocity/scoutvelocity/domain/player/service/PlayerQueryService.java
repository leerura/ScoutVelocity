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
 * 선수 조회 서비스 (Step 1: Plain - 비효율의 끝판왕)
 *
 * 전략: findAll()로 전체 데이터(100,000건)를 메모리에 로드한 후 Stream으로 필터링
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
     */
    public List<PlayerResponseDto> findByClub(Long clubId) {
        log.info("[PLAIN] 클럽별 조회 시작 - clubId: {}", clubId);

        List<Player> allPlayers = playerRepository.findAll();
        log.info("[PLAIN] 전체 선수 로드 완료 - 총 {}건", allPlayers.size());

        List<PlayerResponseDto> result = allPlayers.stream()
                .filter(p -> p.getClub() != null && p.getClub().getId().equals(clubId))
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        log.info("[PLAIN] 클럽별 조회 완료 - 결과 {}건", result.size());
        return result;
    }

    /**
     * 시나리오 2: 포지션 + 복합 능력치 필터링
     */
    public List<PlayerResponseDto> findByPositionAndStats(
            String position,
            Integer minPace,
            Integer minShooting,
            Integer minPhysical
    ) {
        log.info("[PLAIN] 복합 필터링 조회 시작");

        List<Player> allPlayers = playerRepository.findAll();

        List<PlayerResponseDto> result = allPlayers.stream()
                .filter(p -> position == null ||
                        (p.getPlayerPositions() != null && p.getPlayerPositions().contains(position)))
                .filter(p -> minPace == null ||
                        (p.getStats().getPace() != null && p.getStats().getPace() >= minPace))
                .filter(p -> minShooting == null ||
                        (p.getStats().getShooting() != null && p.getStats().getShooting() >= minShooting))
                .filter(p -> minPhysical == null ||
                        (p.getStats().getPhysic() != null && p.getStats().getPhysic() >= minPhysical))
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        log.info("[PLAIN] 복합 필터링 완료 - 결과 {}건", result.size());
        return result;
    }

    /**
     * 시나리오 3: 연령대 + 포텐셜 기반 유망주 발굴 (정렬 포함)
     */
    public List<PlayerResponseDto> findYoungTalents(
            Integer minAge,
            Integer maxAge,
            Integer minPotential,
            String sortBy,
            String order
    ) {
        log.info("[PLAIN] 유망주 발굴 시작");

        List<Player> allPlayers = playerRepository.findAll();

        List<PlayerResponseDto> filtered = allPlayers.stream()
                .filter(p -> minAge == null || (p.getAge() != null && p.getAge() >= minAge))
                .filter(p -> maxAge == null || (p.getAge() != null && p.getAge() <= maxAge))
                .filter(p -> minPotential == null || (p.getPotential() != null && p.getPotential() >= minPotential))
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        Comparator<PlayerResponseDto> comparator = getComparator(sortBy != null ? sortBy : "overall");
        if ("asc".equalsIgnoreCase(order)) {
            filtered.sort(comparator);
        } else {
            filtered.sort(comparator.reversed());
        }

        log.info("[PLAIN] 유망주 발굴 완료 - 결과 {}건", filtered.size());
        return filtered;
    }

    /**
     * 시나리오 4: 리그 + 국적 + 페이징 (복합 조건)
     */
    public PageResponseDto<PlayerResponseDto> findByLeagueAndNationality(
            Long leagueId,
            Long nationalityId,
            int page,
            int size
    ) {
        log.info("[PLAIN] 리그+국적 조회 시작");

        List<Player> allPlayers = playerRepository.findAll();

        List<PlayerResponseDto> filtered = allPlayers.stream()
                .filter(p -> leagueId == null ||
                        (p.getClub() != null && p.getClub().getLeague() != null &&
                                p.getClub().getLeague().getId().equals(leagueId)))
                .filter(p -> nationalityId == null ||
                        (p.getNationality() != null && p.getNationality().getId().equals(nationalityId)))
                .map(PlayerResponseDto::from)
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        List<PlayerResponseDto> pageContent = filtered.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<PlayerResponseDto> resultPage = new PageImpl<>(pageContent, pageable, filtered.size());

        log.info("[PLAIN] 리그+국적 조회 완료 - 전체 {}건", filtered.size());
        return PageResponseDto.of(resultPage);
    }

    /**
     * 시나리오 5: 선수별 경기 기록 조회 (JOIN 비효율)
     *
     * 비효율 포인트:
     * 1. Player 전체 로드 (100,000건) - 검증용
     * 2. MatchRecord 전체 로드 (3,000,000건)
     * 3. 메모리 상에서 Loop 돌며 매칭 (JOIN)
     */
    public List<MatchRecordResponseDto> findMatchRecordsByPlayer(String playerId) {
        log.info("[PLAIN] 경기 기록 조회 시작 - playerId: {}", playerId);

        // 1. 선수 존재 확인을 위해 전체 로드 (비효율의 극치)
        List<Player> allPlayers = playerRepository.findAll();
        boolean playerExists = allPlayers.stream()
                .anyMatch(p -> p.getId().equals(playerId));

        if (!playerExists) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }

        // 2. 모든 경기 기록 로드 (약 300만 건)
        log.info("[PLAIN] 전체 경기 기록 로드 시작 (주의: 메모리 급증 예상)");
        List<MatchRecord> allRecords = matchRecordRepository.findAll();
        log.info("[PLAIN] 전체 경기 기록 로드 완료 - 총 {}건", allRecords.size());

        // 3. 메모리 JOIN (필터링)
        List<MatchRecordResponseDto> result = allRecords.stream()
                .filter(r -> r.getPlayer() != null && r.getPlayer().getId().equals(playerId))
                .map(MatchRecordResponseDto::from) // DTO 변환
                .collect(Collectors.toList());

        log.info("[PLAIN] 경기 기록 조회 완료 - 결과 {}건", result.size());
        return result;
    }

    /**
     * 시나리오 6: 골 + 어시스트 상위 공격수 조회 (메모리 집계)
     *
     * 비효율 포인트:
     * - 300만 건 MatchRecord 전체 로드
     * - 메모리에서 GROUP BY (선수별 집계)
     * - 메모리에서 정렬 & 필터링
     */
    public List<TopScorerResponseDto> findTopScorers(
            String position,
            Integer minGoals,
            Integer minAssists,
            int limit
    ) {
        log.info("[PLAIN] 상위 득점자 집계 시작 - position: {}, minGoals: {}, minAssists: {}", position, minGoals, minAssists);

        // 1. 전체 경기 기록 로드 (IO & 메모리 폭발)
        List<MatchRecord> allRecords = matchRecordRepository.findAll();
        log.info("[PLAIN] 전체 경기 기록 로드 완료 - 총 {}건", allRecords.size());

        // 2. 메모리 상에서 데이터 가공 (CPU 부하)
        // 선수별(Player)로 기록(MatchRecord)을 그룹핑
        Map<Player, List<MatchRecord>> playerRecordsMap = allRecords.stream()
                // 2-1. 포지션 필터링 (N+1 발생 가능: player.getPlayerPositions 접근 시)
                .filter(r -> position == null ||
                        (r.getPlayer() != null && r.getPlayer().getPlayerPositions().contains(position)))
                .collect(Collectors.groupingBy(MatchRecord::getPlayer));

        // 3. 그룹핑된 데이터로 통계 계산 (SUM)
        List<TopScorerResponseDto> result = playerRecordsMap.entrySet().stream()
                .map(entry -> {
                    Player player = entry.getKey();
                    List<MatchRecord> records = entry.getValue();

                    int totalGoals = records.stream().mapToInt(MatchRecord::getGoals).sum();
                    int totalAssists = records.stream().mapToInt(MatchRecord::getAssists).sum();

                    return TopScorerResponseDto.of(player, totalGoals, totalAssists);
                })
                // 4. 집계 후 조건 필터링 (HAVING 절을 메모리에서 처리)
                .filter(dto -> minGoals == null || dto.getTotalGoals() >= minGoals)
                .filter(dto -> minAssists == null || dto.getTotalAssists() >= minAssists)
                // 5. 정렬 (ORDER BY를 메모리에서 처리)
                .sorted(Comparator.comparingInt(TopScorerResponseDto::getTotalPoints).reversed())
                // 6. 개수 제한 (LIMIT)
                .limit(limit)
                .collect(Collectors.toList());

        log.info("[PLAIN] 상위 득점자 집계 완료 - 결과 {}건", result.size());
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

    /**
     * 시나리오 7: 최근 폼 상태 조회 (날짜 범위 + 집계)
     *
     * 비효율 포인트:
     * - 300만 건 전체 로드 (findAll)
     * - 날짜 범위 필터링을 메모리에서 수행 (DB Index 미사용)
     * - 선수별 평균 계산 (메모리)
     * - 정렬 (메모리)
     */
    public List<RecentFormResponseDto> findRecentForm(
            LocalDate startDate,
            LocalDate endDate,
            int minMatches,
            int limit
    ) {
        log.info("[PLAIN] 최근 폼 조회 시작 - {} ~ {}, minMatches: {}", startDate, endDate, minMatches);

        // 1. 전체 경기 기록 로드 (300만 건) - 역시나 여기서부터 헬게이트 오픈
        List<MatchRecord> allRecords = matchRecordRepository.findAll();
        log.info("[PLAIN] 전체 경기 기록 로드 완료 - 총 {}건", allRecords.size());

        // 2. 메모리에서 날짜 필터링 & 선수별 그룹핑
        Map<Player, List<MatchRecord>> playerRecordsMap = allRecords.stream()
                // 날짜 비교 (DB에서 하면 0.001초인걸 여기서 루프 돌려서 찾음)
                .filter(r -> {
                    LocalDate matchDate = r.getMatchDate();
                    return !matchDate.isBefore(startDate) && !matchDate.isAfter(endDate);
                })
                .filter(r -> r.getPlayer() != null)
                .collect(Collectors.groupingBy(MatchRecord::getPlayer));

        // 3. 통계 계산 (평균 평점, 골, 어시스트)
        List<RecentFormResponseDto> result = playerRecordsMap.entrySet().stream()
                .map(entry -> {
                    Player player = entry.getKey();
                    List<MatchRecord> records = entry.getValue();

                    int matches = records.size();
                    // 경기 수가 부족하면 null 리턴해서 아래에서 필터링
                    if (matches < minMatches) return null;

                    int totalGoals = records.stream().mapToInt(MatchRecord::getGoals).sum();
                    int totalAssists = records.stream().mapToInt(MatchRecord::getAssists).sum();
                    double totalRating = records.stream()
                            .map(MatchRecord::getRating)
                            .filter(java.util.Objects::nonNull)
                            .mapToDouble(BigDecimal::doubleValue)
                            .sum();

                    return RecentFormResponseDto.of(player, matches, totalRating, totalGoals, totalAssists);
                })
                .filter(java.util.Objects::nonNull) // 경기 수 부족한 선수 제외
                // 4. 평균 평점 높은 순 정렬
                .sorted(Comparator.comparingDouble(RecentFormResponseDto::getAvgRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        log.info("[PLAIN] 최근 폼 조회 완료 - 결과 {}건", result.size());
        return result;
    }
}