package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.VerifyEmailRequestDto;
import com.tj.GFV_Map.dto.request.ResendVerificationRequestDto;
import com.tj.GFV_Map.dto.request.*;
import com.tj.GFV_Map.dto.response.TokenResponseDto;
import com.tj.GFV_Map.service.AuthService;
import com.tj.GFV_Map.service.GoogleOAuthService;
import com.tj.GFV_Map.service.KakaoOAuthService;
import com.tj.GFV_Map.service.NaverOAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.reissue(dto.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    // kakao return
    private final KakaoOAuthService kakaoOAuthService;  // 필드 추가
    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenResponseDto> kakaoCallback(@RequestParam String code) {
        return ResponseEntity.ok(kakaoOAuthService.loginWithKakao(code));
    }

    // Google return
    // 필드 추가 (위쪽 KakaoOAuthService 옆에)
    private final GoogleOAuthService googleOAuthService;

    // 메서드 추가
    @GetMapping("/google/callback")
    public ResponseEntity<TokenResponseDto> googleCallback(@RequestParam String code) {
        return ResponseEntity.ok(googleOAuthService.loginWithGoogle(code));
    }

    // Naver return
    // 필드 추가
    private final NaverOAuthService naverOAuthService;

    // 메서드 추가 — state도 같이 받음
    @GetMapping("/naver/callback")
    public ResponseEntity<TokenResponseDto> naverCallback(
            @RequestParam String code,
            @RequestParam String state) {
        return ResponseEntity.ok(naverOAuthService.loginWithNaver(code, state));
    }

    //이멜 코드 검증
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequestDto dto) {
        authService.verifyEmail(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    // 인증 코드 재발송
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestBody ResendVerificationRequestDto dto) {
        authService.sendVerificationCode(dto.getEmail());
        return ResponseEntity.ok("인증 코드가 재발송되었습니다.");
    }
}
