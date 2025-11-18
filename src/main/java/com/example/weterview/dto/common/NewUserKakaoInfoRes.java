package com.example.weterview.dto.common;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewUserKakaoInfoRes extends OurMemberDto {
    private String kakaoUniqueId;
    private String kakaoEmail;
}
