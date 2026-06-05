package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.UserUpdateRequestDto;
import com.tj.GFV_Map.dto.response.UserResponseDto;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponseDto.from(user);
    }

    // 프로필 수정 (닉네임, 자기소개, 사진)
    @Transactional
    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateProfile(dto.getNickname(), dto.getProfileImageUrl(), dto.getBio());
        return UserResponseDto.from(user);  // 더티체킹으로 자동 UPDATE
    }
}