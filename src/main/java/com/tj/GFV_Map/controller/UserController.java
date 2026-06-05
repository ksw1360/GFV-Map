package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.UserUpdateRequestDto;
import com.tj.GFV_Map.dto.response.UserResponseDto;
import com.tj.GFV_Map.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getMyInfo(userId));
    }

    // 프로필 수정
    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateProfile(userId, dto));
    }
}