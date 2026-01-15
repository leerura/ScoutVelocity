package com.scoutvelocity.scoutvelocity.domain.player.service;

import com.scoutvelocity.scoutvelocity.domain.matchrecord.MatchRecord;
import com.scoutvelocity.scoutvelocity.domain.matchrecord.dto.MatchRecordResponseDto;
import com.scoutvelocity.scoutvelocity.domain.matchrecord.repository.MatchRecordRepository;
import com.scoutvelocity.scoutvelocity.domain.player.Player;
import com.scoutvelocity.scoutvelocity.domain.player.dto.PlayerResponseDto;
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

import java.util.Comparator;
import java.util.List;
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