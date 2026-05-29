package com.tj.GFV_Map.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @Column(name = "rt_user_id")
    private Long userId;   // users.user_id 와 1:1 (PK = FK)

    @Column(name = "rt_token", nullable = false, length = 500)
    private String token;

    @Column(name = "rt_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "rt_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public RefreshToken(Long userId, String token, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    // Rotation 시 토큰 값과 만료 시각만 교체
    public void rotate(String newToken, LocalDateTime newExpiresAt) {
        this.token = newToken;
        this.expiresAt = newExpiresAt;
        this.createdAt = LocalDateTime.now();
    }
}
