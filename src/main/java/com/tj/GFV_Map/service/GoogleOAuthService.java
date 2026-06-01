package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.GoogleUserInfoDto;
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
public class GoogleOAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${google.client-id}")
    private String clientId;
    @Value("${google.client-secret}")
    private String clientSecret;
    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Transactional
    public TokenResponseDto loginWithGoogle(String code) {
        String googleAccessToken = getGoogleAccessToken(code);
        GoogleUserInfoDto userInfo = getGoogleUserInfo(googleAccessToken);
        User user = findOrCreateUser(userInfo);
        return issueOurTokens(user);
    }

    // 1) 인가코드 → 구글 access token
    private String getGoogleAccessToken(String code) {
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
                "https://oauth2.googleapis.com/token", request, Map.class);

        if (response.getBody() == null || response.getBody().get("access_token") == null) {
            throw new IllegalStateException("구글 토큰 발급 실패");
        }
        return (String) response.getBody().get("access_token");
    }

    // 2) 구글 access token → 사용자 정보
    private GoogleUserInfoDto getGoogleUserInfo(String googleAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfoDto> response = restTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.GET, request, GoogleUserInfoDto.class);

        if (response.getBody() == null || response.getBody().getSub() == null) {
            throw new IllegalStateException("구글 사용자 정보 조회 실패");
        }
        return response.getBody();
    }

    // 3) DB 등록 또는 조회 (구글도 가짜 이메일로 식별 — 결정 사항)
    private User findOrCreateUser(GoogleUserInfoDto info) {
        String fakeEmail = "google_" + info.getSub() + "@gfvmap.local";

        return userRepository.findByEmail(fakeEmail)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(fakeEmail)
                            .password(null)
                            .nickname(info.getName() != null ? info.getName() : "구글사용자")
                            .profileImageUrl(info.getPicture())
                            .provider(UserProvider.GOOGLE)
                            .role(UserRole.USER)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    // 4) 우리 JWT 발급 (카카오와 완전 동일)
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