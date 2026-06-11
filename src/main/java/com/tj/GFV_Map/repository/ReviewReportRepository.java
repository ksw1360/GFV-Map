package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.ReviewReport;
import com.tj.GFV_Map.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    // 한 사용자가 한 리뷰에 신고했는지 (중복 체크)
    Optional<ReviewReport> findByReviewIdAndUserId(Long reviewId, Long userId);

    // 관리자용: 상태별 신고 목록 (페이징, 최신순)
    Page<ReviewReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    // 관리자용: 전체 신고 목록 (페이징, 최신순)
    Page<ReviewReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 관리자용: 삭제된 리뷰의 신고는 제외 (전체)
    @Query("SELECT rr FROM ReviewReport rr WHERE rr.review.isDeleted = false ORDER BY rr.createdAt DESC")
    Page<ReviewReport> findActiveByOrderByCreatedAtDesc(Pageable pageable);

    // 관리자용: 삭제된 리뷰의 신고는 제외 (상태별)
    @Query("SELECT rr FROM ReviewReport rr WHERE rr.review.isDeleted = false AND rr.status = :status ORDER BY rr.createdAt DESC")
    Page<ReviewReport> findActiveByStatusOrderByCreatedAtDesc(@Param("status") ReportStatus status, Pageable pageable);
}