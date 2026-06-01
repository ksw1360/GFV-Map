package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfoDto {
    private String resultcode;
    private String message;
    private Response response;

    // 편의 메서드들
    public String getId() {
        return response != null ? response.id : null;
    }
    public String getName() {
        return response != null ? response.name : null;
    }
    public String getNickname() {
        return response != null ? response.nickname : null;
    }
    public String getProfileImage() {
        return response != null ? response.profile_image : null;
    }
    public String getEmail() {
        return response != null ? response.email : null;
    }

    @Getter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private String id;             // 네이버 고유 ID (필수)
        private String name;
        private String nickname;
        private String email;
        private String profile_image;
    }
}