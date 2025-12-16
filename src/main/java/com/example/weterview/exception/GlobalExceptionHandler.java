package com.example.weterview.exception;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 로직 예외 (CustomException)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        log.warn("CustomException: {}", e.getResultCode().getMessage());
        return ResponseEntity
                .status(e.getResultCode().getHttpStatus())
                .body(ApiResponse.fail(e.getResultCode()));
    }

    // @Valid 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

        log.warn("Validation Failed: {}", errorMessage);

        return ResponseEntity
                .status(ResultCode.INVALID_INPUT_VALUE.getHttpStatus())
                .body(ApiResponse.fail(ResultCode.INVALID_INPUT_VALUE, errorMessage));
    }

    // 그 외 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity
                .status(ResultCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ResultCode.INTERNAL_SERVER_ERROR));
    }
}