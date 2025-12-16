package com.example.weterview.exception;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [핵심] 비즈니스 로직 예외 (CustomException)
     * 우리가 정의한 비즈니스 코드로 응답을 내려주고,
     * 로그에는 "어떤 데이터" 때문에 터졌는지 상세히 남김.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        ResultCode resultCode = e.getResultCode();
        Object data = e.getData();

        // ⭐️ 실무 로그 패턴: [에러코드] 메시지 -> 상세 데이터
        // 예: [USER_404] 존재하지 않는 사용자입니다 -> {userId=15}
        if (data != null) {
            log.warn("[{}] {} -> {}", resultCode.getCode(), resultCode.getMessage(), data);
        } else {
            log.warn("[{}] {}", resultCode.getCode(), resultCode.getMessage());
        }

        return ResponseEntity
                .status(resultCode.getHttpStatus())
                .body(ApiResponse.of(resultCode));
        // 필요하다면 data를 프론트에게도 내려줄 수 있음: ApiResponse.of(resultCode, data)
    }

    /**
     * @Valid 유효성 검사 실패
     * 사용자가 입력한 값 중 잘못된 필드를 모두 모아서 알려줌
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // 에러 메시지를 하나로 합침 (예: "[email] 형식이 안 맞음, [password] 너무 짧음")
        // 실무에서는 Map<String, String>으로 필드별 에러를 내려주기도 함
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            builder.append(" / "); // 구분자
        }

        String errorMessage = builder.toString();
        // 마지막 구분자 제거 등 포매팅은 취향껏

        log.info("Validation Failed: {}", errorMessage); // 유효성 검사는 warn보다 info로 찍기도 함 (너무 흔해서)

        // 앞서 정의한 validationFail 메서드 사용 (메시지 커스텀 허용된 유일한 창구)
        return ResponseEntity
                .status(ResultCode.INVALID_INPUT_VALUE.getHttpStatus())
                .body(ApiResponse.validationFail(ResultCode.INVALID_INPUT_VALUE, errorMessage));
    }

    /**
     * 그 외 예상치 못한 예외 (최후의 보루)
     * 알 수 없는 런타임 에러는 무조건 서버 잘못(500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        // ⭐️ 중요: 500 에러는 무조건 ERROR 레벨로 스택트레이스를 다 찍어야 함
        // 모니터링 툴(Sentry, Slack) 알림의 트리거가 됨
        log.error("Unhandled Exception Occurred: ", e);

        return ResponseEntity
                .status(ResultCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.of(ResultCode.INTERNAL_SERVER_ERROR));
    }
}