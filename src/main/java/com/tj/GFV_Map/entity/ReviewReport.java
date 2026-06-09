package com.tj.GFV_Map.entity;

import com.tj.GFV_Map.enums.ReportCategory;
import com.tj.GFV_Map.enums.ReportStatus;
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
        name = "review_reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uidx_report_review_user",
                        columnNames = {"report_review_id", "report_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_report_status_created",
                        columnList = "report_status, report_created_at DESC"),
                @Index(name = "idx_report_review", columnList = "report_review_id"),
                @Index(name = "idx_report_user", columnList = "report_user_id")
        }
)
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_user_id", nullable = false)
    private User user;   // 신고자 (OWNER)

    @Enumerated(EnumType.STRING)
    @Column(name = "report_category", nullable = false, length = 20)
    private ReportCategory category;

    @Column(name = "report_detail", columnDefinition = "TEXT")
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false, length = 20)
    private ReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_admin_id")
    private User admin;   // 처리한 관리자 (nullable)

    @Column(name = "report_admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @CreationTimestamp
    @Column(name = "report_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "report_resolved_at")
    private LocalDateTime resolvedAt;

    @Builder
    private ReviewReport(Review review, User user, ReportCategory category, String detail) {
        this.review = review;
        this.user = user;
        this.category = category;
        this.detail = detail;
        this.status = ReportStatus.PENDING;
    }

    // 관리자가 처리완료 (리뷰 숨김처리도 같이)
    public void resolve(User admin, String adminNote) {
        this.status = ReportStatus.RESOLVED;
        this.admin = admin;
        this.adminNote = adminNote;
        this.resolvedAt = LocalDateTime.now();
    }

    // 관리자가 신고 반려 (부당한 신고 → 리뷰 다시 노출)
    public void reject(User admin, String adminNote) {
        this.status = ReportStatus.REJECTED;
        this.admin = admin;
        this.adminNote = adminNote;
        this.resolvedAt = LocalDateTime.now();
    }
}