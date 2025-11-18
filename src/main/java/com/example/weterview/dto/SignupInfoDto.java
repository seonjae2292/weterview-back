package com.example.weterview.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
public class SignupInfoDto {
    @NotBlank
    private String kakaoUserNumber;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,30}$", message = "Nickname must be 2 to 30 characters long and contain only Korean letters, English letters, or digits.")
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{}\\[\\]|\\\\;:'\",.<>/?]).{8,}$")
    @Max(value = 100, message = "The password can be up to 100 digits")
    private String password;
}
