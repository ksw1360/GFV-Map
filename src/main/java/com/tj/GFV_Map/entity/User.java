package com.tj.GFV_Map.entity;

import com.tj.GFV_Map.enums.UserProvider;
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
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uidx_user_email", columnNames = "user_email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email", nullable = false, length = 255)
    private String email;

    @Column(name = "user_password", length = 255)
    private String password;

    @Column(name = "user_nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "user_profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "user_bio", length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_provider", nullable = false, length = 20)
    private UserProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private com.tj.GFV_Map.enums.UserRole role;

    @CreationTimestamp
    @Column(name = "user_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "user_deleted_at")
    private LocalDateTime deletedAt;

    // Mail 추적
    @Column(name = "user_is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Builder
    private User(String email,
                 String password,
                 String nickname,
                 String profileImageUrl,
                 String bio,
                 UserProvider provider,
                 com.tj.GFV_Map.enums.UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.provider = provider;
        this.role = role;
    }

    public void updateProfile(String nickname, String profileImageUrl, String bio) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
    }
}
