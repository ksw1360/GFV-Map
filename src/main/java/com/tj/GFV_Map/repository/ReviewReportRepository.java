package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.ReviewReport;
import com.tj.GFV_Map.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    // 한 사용자가 한 리뷰에 신고했는지 (중복 체크)
    Optional<ReviewReport> findByReviewIdAndUserId(Long reviewId, Long userId);

    // 관리자용: 상태별 신고 목록 (페이징, 최신순)
    Page<ReviewReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    // 관리자용: 전체 신고 목록 (페이징, 최신순)
    Page<ReviewReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
