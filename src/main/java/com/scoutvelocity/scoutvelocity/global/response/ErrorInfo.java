package com.scoutvelocity.scoutvelocity.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 에러 정보
 */
@Getter
@AllArgsConstructor
public class ErrorInfo {
    
    private String code;
    private String message;
    
    public static ErrorInfo of(ErrorCode errorCode) {
        return new ErrorInfo(
            errorCode.getCode(),
            errorCode.getMessage()
        );
    }
    
    public static ErrorInfo of(ErrorCode errorCode, String customMessage) {
        return new ErrorInfo(
            errorCode.getCode(),
            customMessage
        );
    }
}
