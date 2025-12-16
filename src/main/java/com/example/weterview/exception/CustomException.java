package com.example.weterview.exception;

import com.example.weterview.enums.ResultCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ResultCode resultCode;
    // 에러 발생 시 상세 내용을 담을 데이터 (예: 틀린 값, 실패한 ID 등)
    // 이 데이터는 로그에 남기거나, 디버그 모드에서 응답에 포함시킬 수 있음
    private final Object data;

    // 1. 가장 자주 쓰는 생성자: ResultCode만 전달
    public CustomException(ResultCode resultCode) {
        super(resultCode.getMessage()); // 부모에는 무조건 Enum의 메시지를 넘김
        this.resultCode = resultCode;
        this.data = null;
    }

    // 2. 데이터 포함 생성자: "왜" 에러가 났는지 데이터를 같이 넘김
    // 예: throw new CustomException(ResultCode.USER_NOT_FOUND, userId);
    public CustomException(ResultCode resultCode, Object data) {
        super(resultCode.getMessage()); // 메시지 변조 금지!
        this.resultCode = resultCode;
        this.data = data;
    }
}