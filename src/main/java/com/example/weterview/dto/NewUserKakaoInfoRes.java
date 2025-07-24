package com.example.weterview.dto;

import com.example.weterview.dto.common.OurMemberDto;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewUserKakaoInfoRes extends OurMemberDto {
    private String kakaoUniqueId;
    private String kakaoEmail;
}
