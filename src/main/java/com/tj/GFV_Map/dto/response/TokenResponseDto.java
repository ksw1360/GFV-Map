package com.tj.GFV_Map.dto.response;

import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String profileImageUrl;
    private UserRole role;
    private String email;
}
