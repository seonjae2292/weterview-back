package com.example.weterview.dto.common.response;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewUserKakaoInfoRes extends OurMemberDto {
    private String kakaoUniqueId;
    private String kakaoEmail;
}
