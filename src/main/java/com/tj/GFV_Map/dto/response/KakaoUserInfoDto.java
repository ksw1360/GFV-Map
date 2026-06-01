package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 카카오가 보내는 다른 필드 무시
public class KakaoUserInfoDto {
    private Long id;                  // 카카오 고유 ID (필수)
    private KakaoAccount kakao_account;

    public String getNickname() {
        if (kakao_account == null || kakao_account.profile == null) return null;
        return kakao_account.profile.nickname;
    }

    public String getProfileImageUrl() {
        if (kakao_account == null || kakao_account.profile == null) return null;
        return kakao_account.profile.profile_image_url;
    }

    @Getter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private Profile profile;
    }

    @Getter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String nickname;
        private String profile_image_url;
    }
}