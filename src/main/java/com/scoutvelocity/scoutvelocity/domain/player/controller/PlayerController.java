package com.scoutvelocity.scoutvelocity.domain.player.controller;

import com.scoutvelocity.scoutvelocity.domain.player.dto.PlayerResponseDto;
import com.scoutvelocity.scoutvelocity.domain.player.service.PlayerQueryService;
import com.scoutvelocity.scoutvelocity.global.response.ApiResponse;
import com.scoutvelocity.scoutvelocity.global.response.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}