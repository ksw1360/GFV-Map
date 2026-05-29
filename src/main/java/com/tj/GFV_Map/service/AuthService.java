package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.LoginRequestDto;
import com.tj.GFV_Map.dto.request.SignupRequestDto;
import com.tj.GFV_Map.dto.response.TokenResponseDto;
import com.tj.GFV_Map.entity.RefreshToken;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserProvider;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.jwt.JwtTokenProvider;
import com.tj.GFV_Map.repository.RefreshTokenRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;  // ← 추가
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입은 그대로
    @Transactional
    public void signup(SignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .provider(UserProvider.LOCAL)
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
    }

    // ── 로그인: 토큰 발급 + refresh 를 DB 에 저장 ──
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        user.updateLastLoginAt();

        String accessToken  = jwtTokenProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        LocalDateTime refreshExpiresAt = jwtTokenProvider.getExpiration(refreshToken);

        // 기존 토큰이 있으면 rotate, 없으면 새로 저장 (한 사용자당 1개 유지)
        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        existing -> existing.rotate(refreshToken, refreshExpiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(user.getId())
                                        .token(refreshToken)
                                        .expiresAt(refreshExpiresAt)
                                        .build())
                );

        return new TokenResponseDto(accessToken, refreshToken);
    }

    // ── 재발급(Rotation): refresh 검증 → DB 대조 → 새 access + 새 refresh 발급 ──
    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        // 1) 서명·만료 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token 입니다.");
        }

        // 2) 토큰에서 userId 꺼내기
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // 3) DB 에 저장된 refresh 와 일치하는지 확인 (탈취·재사용 방지의 핵심)
        RefreshToken stored = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보가 없습니다. 다시 로그인 해주세요."));

        if (!stored.getToken().equals(refreshToken)) {
            // 이미 한 번 회전된 옛날 토큰이거나 위조된 토큰
            // 의심스러우면 그 사용자의 refresh 전부 제거 → 강제 재로그인 유도
            refreshTokenRepository.deleteById(userId);
            throw new IllegalArgumentException("다시 로그인 해주세요.");
        }

        // 4) 사용자 정보 조회 (새 access 에 role/email 담아야 하므로)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 5) 새 access + 새 refresh 발급
        String newAccess  = jwtTokenProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole());
        String newRefresh = jwtTokenProvider.createRefreshToken(user.getId());
        LocalDateTime newRefreshExpiresAt = jwtTokenProvider.getExpiration(newRefresh);

        // 6) DB 의 기존 행을 회전(UPDATE) → 옛 refresh 즉시 무효화
        stored.rotate(newRefresh, newRefreshExpiresAt);

        return new TokenResponseDto(newAccess, newRefresh);
    }

    // 사용자 Token 삭제 - 로그아웃
    @Transactional
    public void logout(Long userId) {
        if(userId==null)
        {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }
        // 해당 사용자의 refresh token 삭제 → 더 이상 재발급 불가
        refreshTokenRepository.deleteById(userId);
    }
}