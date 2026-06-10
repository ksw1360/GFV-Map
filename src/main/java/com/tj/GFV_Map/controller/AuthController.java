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
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;  // 위쪽 import에 추가

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
    private final KakaoOAuthService kakaoOAuthService; // 필드 추가

    @GetMapping("/kakao/callback")
    public RedirectView kakaoCallback(@RequestParam String code) {
        TokenResponseDto token = kakaoOAuthService.loginWithKakao(code);

        String redirect = "http://localhost:3000/oauth/callback"
                + "#accessToken=" + token.getAccessToken()
                + "&refreshToken=" + token.getRefreshToken();

        return new RedirectView(redirect);
    }

    // 👇 새로 추가 — SPA 프론트가 POST로 code 보내는 패턴
    @PostMapping("/kakao/login")
    public ResponseEntity<TokenResponseDto> kakaoLoginPost(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        return ResponseEntity.ok(kakaoOAuthService.loginWithKakao(code));
    }

    // Google return
    // 필드 추가 (위쪽 KakaoOAuthService 옆에)
    private final GoogleOAuthService googleOAuthService;

    // 메서드 추가
    @GetMapping("/google/callback")
    public RedirectView googleCallback(@RequestParam String code) {
        TokenResponseDto token = googleOAuthService.loginWithGoogle(code);

        String redirect = "http://localhost:3000/oauth/callback"
                + "#accessToken=" + token.getAccessToken()
                + "&refreshToken=" + token.getRefreshToken();

        return new RedirectView(redirect);
    }

    // Naver return
    // 필드 추가
    private final NaverOAuthService naverOAuthService;

    // 메서드 추가 — state도 같이 받음
    @GetMapping("/naver/callback")
    public RedirectView naverCallback(@RequestParam String code,
                                      @RequestParam String state) {
        TokenResponseDto token = naverOAuthService.loginWithNaver(code, state);

        String redirect = "http://localhost:3000/oauth/callback"
                + "#accessToken=" + token.getAccessToken()
                + "&refreshToken=" + token.getRefreshToken();

        return new RedirectView(redirect);
    }

    // 이멜 코드 검증 사용안함
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

    // 가입 1단계: 이메일 입력 → 인증 코드 발송
    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmailCode(@Valid @RequestBody SendEmailCodeRequestDto dto) {
        authService.sendEmailCodeForSignup(dto.getEmail());
        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    // 가입 2단계: 코드 검증 (사용 처리 안 함)
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmailCode(@RequestBody VerifyEmailRequestDto dto) {
        authService.verifyCodeOnly(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok("이메일이 인증되었습니다.");
    }

    // 구글 SPA 로그인
    @PostMapping("/google/login")
    public ResponseEntity<TokenResponseDto> googleLoginPost(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(googleOAuthService.loginWithGoogle(body.get("code")));
    }

    // 네이버 SPA 로그인 (state도 받음)
    @PostMapping("/naver/login")
    public ResponseEntity<TokenResponseDto> naverLoginPost(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");
        return ResponseEntity.ok(naverOAuthService.loginWithNaver(code, state));
    }
}
