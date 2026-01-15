package com.scoutvelocity.scoutvelocity.domain.player.controller;

import com.scoutvelocity.scoutvelocity.domain.matchrecord.dto.MatchRecordResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.dto.PlayerResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.dto.RecentFormResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.dto.TopScorerResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.service.PlayerQueryService;
import com.scoutvelocity.scoutvelocity.global.response.ApiResponse;
import com.scoutvelocity.scoutvelocity.global.response.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 선수 조회 API Controller (Step 1: Plain)
 *
 * 4개 시나리오:
 * 1. 클럽별 조회
 * 2. 포지션 + 복합 능력치 필터링
 * 3. 연령대 + 포텐셜 기반 유망주 발굴
 * 4. 리그 + 국적 + 페이징
 */
@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerQueryService playerQueryService;

    /**
     * 시나리오 1: 클럽별 선수 조회
     *
     * GET /api/v1/players/club/{clubId}
     *
     * 예시:
     * - GET /api/v1/players/club/10 (Manchester City)
     * - GET /api/v1/players/club/243 (Real Madrid)
     */
    @GetMapping("/club/{clubId}")
    public ApiResponse<List<PlayerResponseDto>> getPlayersByClub(
            @PathVariable Long clubId
    ) {
        List<PlayerResponseDto> players = playerQueryService.findByClub(clubId);
        return ApiResponse.success(players);
    }

    /**
     * 시나리오 2: 포지션 + 복합 능력치 필터링
     *
     * GET /api/v1/players/search
     *
     * Query Parameters:
     * - position: 포지션 (예: ST, CAM, CB)
     * - minPace: 최소 주력
     * - minShooting: 최소 슈팅
     * - minPhysical: 최소 피지컬
     *
     * 예시:
     * - GET /api/v1/players/search?position=ST&minPace=85&minShooting=80&minPhysical=75
     */
    @GetMapping("/search")
    public ApiResponse<List<PlayerResponseDto>> searchPlayers(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer minPace,
            @RequestParam(required = false) Integer minShooting,
            @RequestParam(required = false) Integer minPhysical
    ) {
        List<PlayerResponseDto> players = playerQueryService.findByPositionAndStats(
                position, minPace, minShooting, minPhysical
        );
        return ApiResponse.success(players);
    }

    /**
     * 시나리오 3: 연령대 + 포텐셜 기반 유망주 발굴
     *
     * GET /api/v1/players/talents
     *
     * Query Parameters:
     * - minAge: 최소 나이
     * - maxAge: 최대 나이
     * - minPotential: 최소 포텐셜
     * - sortBy: 정렬 기준 (potential, overall, age, value)
     * - order: 정렬 순서 (asc, desc)
     *
     * 예시:
     * - GET /api/v1/players/talents?minAge=18&maxAge=23&minPotential=85&sortBy=potential&order=desc
     */
    @GetMapping("/talents")
    public ApiResponse<List<PlayerResponseDto>> findYoungTalents(
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Integer minPotential,
            @RequestParam(required = false, defaultValue = "overall") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        List<PlayerResponseDto> players = playerQueryService.findYoungTalents(
                minAge, maxAge, minPotential, sortBy, order
        );
        return ApiResponse.success(players);
    }

    /**
     * 시나리오 4: 리그 + 국적 + 페이징
     *
     * GET /api/v1/players
     *
     * Query Parameters:
     * - leagueId: 리그 ID
     * - nationalityId: 국적 ID
     * - page: 페이지 번호 (0부터 시작)
     * - size: 페이지 크기
     *
     * 예시:
     * - GET /api/v1/players?leagueId=13&nationalityId=45&page=0&size=20
     */
    @GetMapping
    public ApiResponse<PageResponseDto<PlayerResponseDto>> getPlayers(
            @RequestParam(required = false) Long leagueId,
            @RequestParam(required = false) Long nationalityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponseDto<PlayerResponseDto> players = playerQueryService.findByLeagueAndNationality(
                leagueId, nationalityId, page, size
        );
        return ApiResponse.success(players);
    }

    /**
     * 시나리오 5: 선수별 경기 기록 조회 (JOIN 비효율 테스트)
     *
     * GET /api/v1/players/{playerId}/match-records
     *
     * 목적: 특정 선수의 모든 경기 기록 조회
     * 비효율: 300만 건의 경기 기록을 메모리에 로드 후 필터링
     *
     * 예시:
     * - GET /api/v1/players/231747/match-records (K. Mbappé)
     */
    @GetMapping("/{playerId}/match-records")
    public ApiResponse<List<MatchRecordResponseDto>> getMatchRecords(
            @PathVariable String playerId
    ) {
        List<MatchRecordResponseDto> records = playerQueryService.findMatchRecordsByPlayer(playerId);
        return ApiResponse.success(records);
    }

    /**
     * 시나리오 6: 골 + 어시스트 상위 공격수 (집계 쿼리 비효율 테스트)
     *
     * GET /api/v1/players/top-scorers
     *
     * Query Parameters:
     * - position: 포지션 (예: ST, RW, LW)
     * - minGoals: 최소 득점
     * - minAssists: 최소 도움
     * - limit: 조회 개수 (기본 20)
     *
     * 예시:
     * - GET /api/v1/players/top-scorers?position=ST&minGoals=10&minAssists=5&limit=10
     */
    @GetMapping("/top-scorers")
    public ApiResponse<List<TopScorerResponseDto>> getTopScorers(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer minGoals,
            @RequestParam(required = false) Integer minAssists,
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<TopScorerResponseDto> result = playerQueryService.findTopScorers(
                position, minGoals, minAssists, limit
        );
        return ApiResponse.success(result);
    }

    /**
     * 시나리오 7: 최근 폼 상태 조회 (날짜 범위 + 집계 비효율 테스트)
     *
     * GET /api/v1/players/recent-form
     *
     * Query Parameters:
     * - startDate: 시작일 (YYYY-MM-DD)
     * - endDate: 종료일 (YYYY-MM-DD)
     * - minMatches: 최소 경기 수
     * - limit: 조회 개수
     *
     * 예시:
     * - GET /api/v1/players/recent-form?startDate=2024-01-01&endDate=2024-03-31&minMatches=5
     */
    @GetMapping("/recent-form")
    public ApiResponse<List<RecentFormResponseDto>> getRecentForm(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "5") int minMatches,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // 기본값 설정 (최근 3개월)
        if (startDate == null) startDate = LocalDate.now().minusMonths(3);
        if (endDate == null) endDate = LocalDate.now();

        List<RecentFormResponseDto> result = playerQueryService.findRecentForm(
                startDate, endDate, minMatches, limit
        );
        return ApiResponse.success(result);
    }
}