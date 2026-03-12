package com.portfolio.fastbreak.global.error;

import com.portfolio.fastbreak.global.common.ApiResponse;
import com.portfolio.fastbreak.global.error.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 정의한 비즈니스 로직 에러 처리
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.error("BusinessException : {}", e.getMessage());
        return ApiResponse.error((e.getErrorCode().getStatus()), e.getMessage());
    }

    // 그 외 예상치 못한 서버 에러 처리
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGeneralException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }
}
