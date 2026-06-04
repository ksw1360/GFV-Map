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
        name = "notices",
        indexes = {
                @Index(name = "idx_notice_visible_pinned_created",
                        columnList = "notice_is_visible, notice_is_pinned DESC, notice_created_at DESC"),
                @Index(name = "idx_notice_category", columnList = "notice_category")
        }
)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_author_id", nullable = false)
    private User author;   // 작성 관리자

    @Column(name = "notice_title", nullable = false, length = 200)
    private String title;

    @Column(name = "notice_content", columnDefinition = "TEXT", nullable = false)
    private String content;

    // 한글 ENUM — MySQL ENUM 타입으로, Java에선 String
    @Column(name = "notice_category", nullable = false,
            columnDefinition = "ENUM('서비스','점검','업데이트','기타')")
    private String category;

    @Column(name = "notice_is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "notice_is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "notice_view_count", nullable = false)
    private Integer viewCount = 0;

    @CreationTimestamp
    @Column(name = "notice_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "notice_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Notice(User author, String title, String content, String category,
                   Boolean isPinned) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.category = category;
        this.isPinned = isPinned != null ? isPinned : false;
        this.isVisible = true;
        this.viewCount = 0;
    }

    public void update(String title, String content, String category, Boolean isPinned) {
        this.title = title;
        this.content = content;
        this.category = category;
        if (isPinned != null) this.isPinned = isPinned;
    }

    public void hide() {
        this.isVisible = false;
    }

    public void show() {
        this.isVisible = true;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}