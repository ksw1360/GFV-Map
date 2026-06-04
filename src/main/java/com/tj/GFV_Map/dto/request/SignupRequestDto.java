package com.tj.GFV_Map.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;            // 이메일 인증 코드 (유지 — 프론트가 빠뜨렸지만 필수)

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phone;           // LOCAL 가입 필수

    // 선택 필드
    private String profileImageUrl; // 프론트의 profilePic
    private String bio;
}