package com.tj.GFV_Map.dto.response;

import com.tj.GFV_Map.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private String phone;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .phone(user.getPhone())
                .build();
    }
}