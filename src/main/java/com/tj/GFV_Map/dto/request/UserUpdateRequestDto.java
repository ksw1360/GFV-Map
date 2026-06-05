package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String nickname;
    private String bio;
    private String profileImageUrl;   // 사진 (URL)
}