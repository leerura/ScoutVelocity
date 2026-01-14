package com.scoutvelocity.scoutvelocity.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * API 공통 응답 포맷
 * 
 * 모든 API 응답은 이 형식으로 통일
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private Boolean success;
    private T data;
    private ErrorInfo error;
    private String timestamp;
    
    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            true,
            data,
            null,
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
    
    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static ApiResponse<Void> success() {
        return success(null);
    }
    
    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
        return new ApiResponse<>(
            false,
            null,
            ErrorInfo.of(errorCode),
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
    
    /**
     * 실패 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(
            false,
            null,
            ErrorInfo.of(errorCode, customMessage),
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
}
