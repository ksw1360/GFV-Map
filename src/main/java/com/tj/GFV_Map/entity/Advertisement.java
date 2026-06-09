package com.tj.GFV_Map.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "advertisements",
        indexes = {
                @Index(name = "idx_ad_active_created",
                        columnList = "ad_is_active, ad_created_at DESC")
        }
)
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_author_id", nullable = false)
    private User author;   // 등록 관리자

    @Column(name = "ad_title", nullable = false, length = 200)
    private String title;

    // 광고 이미지 URL (S3 등)
    @Column(name = "ad_image_url", nullable = false, length = 2048)
    private String imageUrl;

    // 클릭 시 이동할 링크
    @Column(name = "ad_link_url", length = 2048)
    private String linkUrl;

    // 노출 여부
    @Column(name = "ad_is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "ad_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ad_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Advertisement(User author, String title, String imageUrl,
                          String linkUrl, Boolean isActive) {
        this.author = author;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(String title, String imageUrl, String linkUrl, Boolean isActive) {
        if (title != null) this.title = title;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (linkUrl != null) this.linkUrl = linkUrl;
        if (isActive != null) this.isActive = isActive;
    }
}
