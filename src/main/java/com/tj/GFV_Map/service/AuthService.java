package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.LoginRequestDto;
import com.tj.GFV_Map.dto.request.SignupRequestDto;
import com.tj.GFV_Map.dto.response.TokenResponseDto;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserProvider;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.jwt.JwtTokenProvider;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // ── 회원가입: 비번 해시해서 저장 ──
    @Transactional
    public void signup(SignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // BCrypt 해싱
                .nickname(dto.getNickname())
                .provider(UserProvider.LOCAL)  // 자체 가입
                .role(UserRole.USER)           // 기본 역할
                .build();
        userRepository.save(user);
    }

    // ── 로그인: 비번 비교 → 토큰 발급 ──
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 입력 비번(평문)과 DB의 해시 비교
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        user.updateLastLoginAt(); // 마지막 로그인 시각 갱신 (엔티티에 있던 메서드)

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new TokenResponseDto(accessToken, refreshToken);
    }
}
