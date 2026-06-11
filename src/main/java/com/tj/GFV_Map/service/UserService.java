package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.UserUpdateRequestDto;
import com.tj.GFV_Map.dto.response.AdminUserResponseDto;
import com.tj.GFV_Map.dto.response.UserResponseDto;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // ===== 회원 탈퇴 (소프트 삭제: deletedAt 세팅) =====
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.softDelete();  // deletedAt = now() — 더티체킹으로 자동 UPDATE
    }

    // ===== 관리자: 전체 사용자 목록 (페이징) =====
    @Transactional(readOnly = true)
    public Page<AdminUserResponseDto> getAllUsers(Long adminId, Pageable pageable) {
        verifyAdmin(adminId);
        return userRepository.findAll(pageable)
                .map(AdminUserResponseDto::from);
    }

    // ===== Helper: 관리자 권한 체크 =====
    private User verifyAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
        return admin;
    }
}