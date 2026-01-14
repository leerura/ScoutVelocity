package com.scoutvelocity.scoutvelocity.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * API 에러 코드 정의
 * 
 * 코드 규칙:
 * - C001~: Common (공통)
 * - P001~: Player (선수)
 * - M001~: MatchRecord (경기 기록)
 * - L001~: League (리그)
 * - N001~: Nationality (국가)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // Common (C001~)
    INTERNAL_SERVER_ERROR("C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT("C002", "잘못된 입력값입니다."),
    RESOURCE_NOT_FOUND("C003", "요청한 리소스를 찾을 수 없습니다."),
    
    // Player (P001~)
    PLAYER_NOT_FOUND("P001", "존재하지 않는 선수입니다."),
    
    // Match Record (M001~)
    MATCH_RECORD_NOT_FOUND("M001", "존재하지 않는 경기 기록입니다."),
    
    // League (L001~)
    LEAGUE_NOT_FOUND("L001", "존재하지 않는 리그입니다."),
    
    // Nationality (N001~)
    NATIONALITY_NOT_FOUND("N001", "존재하지 않는 국가입니다.");
    
    private final String code;
    private final String message;
}
