package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.KakaoUserInfoDto;
import com.tj.GFV_Map.dto.response.TokenResponseDto;
import com.tj.GFV_Map.entity.RefreshToken;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserProvider;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.jwt.JwtTokenProvider;
import com.tj.GFV_Map.repository.RefreshTokenRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // ───────────────────────────────────────────────
    // 진입점: 컨트롤러가 받은 code 로 전체 흐름 처리
    // ───────────────────────────────────────────────
    @Transactional
    public TokenResponseDto loginWithKakao(String code) {
        // 1) code → 카카오 access token
        String kakaoAccessToken = getKakaoAccessToken(code);
        // 2) 카카오 access token → 사용자 정보
        KakaoUserInfoDto userInfo = getKakaoUserInfo(kakaoAccessToken);
        // 3) 우리 DB 에 등록 또는 조회
        User user = findOrCreateUser(userInfo);
        // 4) 우리 JWT 발급
        return issueOurTokens(user);
    }

    // ───────────────────────────────────────────────
    // 1) 인가코드 → 카카오 access token
    // ───────────────────────────────────────────────
    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, Map.class);

        if (response.getBody() == null || response.getBody().get("access_token") == null) {
            throw new IllegalStateException("카카오 토큰 발급 실패");
        }
        return (String) response.getBody().get("access_token");
    }

    // ───────────────────────────────────────────────
    // 2) 카카오 access token → 사용자 정보
    // ───────────────────────────────────────────────
    private KakaoUserInfoDto getKakaoUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET, request, KakaoUserInfoDto.class);

        if (response.getBody() == null || response.getBody().getId() == null) {
            throw new IllegalStateException("카카오 사용자 정보 조회 실패");
        }
        return response.getBody();
    }

    // ───────────────────────────────────────────────
    // 3) DB 등록 또는 조회 (가짜 이메일로 식별)
    // ───────────────────────────────────────────────
    private User findOrCreateUser(KakaoUserInfoDto info) {
        String fakeEmail = "kakao_" + info.getId() + "@gfvmap.local";

        return userRepository.findByEmail(fakeEmail)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(fakeEmail)
                            .password(null) // 카카오 로그인은 비번 없음
                            .nickname(info.getNickname() != null ? info.getNickname() : "카카오사용자")
                            .profileImageUrl(info.getProfileImageUrl())
                            .provider(UserProvider.KAKAO)
                            .role(UserRole.USER)
                            .build();
                    newUser.verifyEmail();   // 👈 이 줄 추가 (소셜은 자동 인증)
                    return userRepository.save(newUser);
                });
    }

    // ───────────────────────────────────────────────
    // 4) 우리 JWT 발급 (자체 로그인이랑 동일한 흐름)
    // ───────────────────────────────────────────────
    private TokenResponseDto issueOurTokens(User user) {
        user.updateLastLoginAt();

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        LocalDateTime refreshExpiresAt = jwtTokenProvider.getExpiration(refreshToken);

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
}