package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoDto {
    // 구글이 평평한 JSON 으로 주기 때문에 내부 클래스 불필요 (카카오보다 단순)
    private String sub;             // 구글 고유 ID (필수)
    private String email;           // 진짜 이메일! 카카오와 달리 받을 수 있음
    private boolean email_verified; // 이메일 인증 여부
    private String name;            // 표시 이름 (닉네임 용도)
    private String picture;         // 프로필 사진 URL
}