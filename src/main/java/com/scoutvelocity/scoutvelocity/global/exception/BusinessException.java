package com.scoutvelocity.scoutvelocity.global.exception;

import com.scoutvelocity.scoutvelocity.global.response.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직 예외
 * 
 * 예상 가능한 비즈니스 에러 상황에서 사용
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
