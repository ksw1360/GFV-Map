package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.NaverUserInfoDto;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverOAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;
    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Transactional
    public TokenResponseDto loginWithNaver(String code, String state) {
        // 1) code → 네이버 access token
        String naverAccessToken = getNaverAccessToken(code, state);
        // 2) 네이버 access token → 사용자 정보
        NaverUserInfoDto userInfo = getNaverUserInfo(naverAccessToken);
        // 3) DB 등록/조회
        User user = findOrCreateUser(userInfo);
        // 4) 우리 JWT 발급
        return issueOurTokens(user);
    }

    // 1) 인가 코드 → access token (네이버는 GET 방식, 쿼리 파라미터로)
    private String getNaverAccessToken(String code, String state) {
        String url = UriComponentsBuilder
                //.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .fromUriString("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null || response.getBody().get("access_token") == null) {
            throw new IllegalStateException("네이버 토큰 발급 실패");
        }
        return (String) response.getBody().get("access_token");
    }

    // 2) access token → 사용자 정보
    private NaverUserInfoDto getNaverUserInfo(String naverAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(naverAccessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserInfoDto> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET, request, NaverUserInfoDto.class);

        if (response.getBody() == null || response.getBody().getId() == null) {
            throw new IllegalStateException("네이버 사용자 정보 조회 실패");
        }
        return response.getBody();
    }

    // 3) DB 등록 또는 조회 (가짜 이메일 정책)
    private User findOrCreateUser(NaverUserInfoDto info) {
        String fakeEmail = "naver_" + info.getId() + "@gfvmap.local";

        // 닉네임 우선순위: nickname → name → 기본값
        String nickname = info.getNickname() != null ? info.getNickname()
                : info.getName() != null ? info.getName()
                  : "네이버사용자";

        return userRepository.findByEmail(fakeEmail)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(fakeEmail)
                            .password(null)
                            .nickname(nickname)
                            .profileImageUrl(info.getProfileImage())
                            .provider(UserProvider.NAVER)
                            .role(UserRole.USER)
                            .build();
                    newUser.verifyEmail();   // 👈 이 줄 추가 (소셜은 자동 인증)
                    return userRepository.save(newUser);
                });
    }

    // 4) 우리 JWT 발급 (카카오·구글과 완전 동일)
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

        return new TokenResponseDto(
                accessToken,
                refreshToken,
                user.getNickname(),          // 👈 추가
                user.getProfileImageUrl(),   // 👈 추가
                user.getRole(),
                user.getEmail()
        );
    }
}