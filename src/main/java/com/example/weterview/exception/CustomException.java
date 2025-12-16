package com.example.weterview.exception;

import com.example.weterview.enums.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ResultCode resultCode;
}
