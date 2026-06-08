package com.tj.GFV_Map.dto.response;

import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.enums.UserProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 관리자 사용자 목록용 DTO.
 * 기존 UserResponseDto와 달리 role, provider, createdAt 등 관리 정보를 포함.
 * (비밀번호는 절대 포함하지 않음)
 */
@Getter
@Builder
public class AdminUserResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private UserRole role;
    private UserProvider provider;  // LOCAL/GOOGLE/KAKAO/NAVER
    private Boolean isEmailVerified;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static AdminUserResponseDto from(User user) {
        return AdminUserResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .provider(user.getProvider())
                .isEmailVerified(user.getIsEmailVerified())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
