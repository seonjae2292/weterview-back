package com.example.weterview.dto.common.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
public class SignupInfoReq {
    @NotBlank(message = "카카오 회원 번호는 필수 입니다.")
    private String kakaoUserNumber;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,30}$", message = "Nickname must be 2 to 30 characters long and contain only Korean letters, English letters, or digits.")
    private String nickname;

    @NotBlank(message = "이메일을 필수 입니다.")
    @Email
    private String kakaoEmail;

    @NotBlank(message = "성별은 필수 입니다.")
    private String gender;
}
