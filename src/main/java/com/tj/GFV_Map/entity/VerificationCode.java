package com.tj.GFV_Map.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "verification_codes",
        indexes = {
                @Index(name = "idx_verification_email", columnList = "verification_email")
        }
)
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    @Column(name = "verification_email", nullable = false, length = 255)
    private String email;

    @Column(name = "verification_code", nullable = false, length = 6)
    private String code;

    @Column(name = "verification_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verification_is_used", nullable = false)
    private Boolean isUsed = false;

    @CreationTimestamp
    @Column(name = "verification_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private VerificationCode(String email, String code, LocalDateTime expiresAt) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
        this.isUsed = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsUsed() {
        this.isUsed = true;
    }
}