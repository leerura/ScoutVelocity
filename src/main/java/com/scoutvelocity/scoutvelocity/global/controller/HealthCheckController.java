package com.scoutvelocity.scoutvelocity.global.controller;

import com.scoutvelocity.scoutvelocity.global.exception.BusinessException;
import com.scoutvelocity.scoutvelocity.global.response.ApiResponse;
import com.scoutvelocity.scoutvelocity.global.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 헬스체크 및 API 응답 포맷 테스트 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {
    
    /**
     * 헬스체크 (성공 응답 테스트)
     * 
     * GET /api/v1/health
     */
    @GetMapping
    public ApiResponse<HealthDto> healthCheck() {
        return ApiResponse.success(
            new HealthDto("UP", "ScoutVelocity API is running", LocalDateTime.now())
        );
    }
    
    /**
     * 에러 응답 테스트 (실패 응답 테스트)
     * 
     * GET /api/v1/health/error
     */
    @GetMapping("/error")
    public ApiResponse<Void> errorTest() {
        throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND);
    }
    
    /**
     * 서버 에러 테스트 (예상치 못한 예외)
     * 
     * GET /api/v1/health/server-error
     */
    @GetMapping("/server-error")
    public ApiResponse<Void> serverErrorTest() {
        throw new RuntimeException("Unexpected error for testing");
    }
    
    @Getter
    @AllArgsConstructor
    static class HealthDto {
        private String status;
        private String message;
        private LocalDateTime timestamp;
    }
}
