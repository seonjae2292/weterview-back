package com.example.weterview.dto.common.response;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class SignupRes extends OurMemberDto {
    private String accessToken;
}
