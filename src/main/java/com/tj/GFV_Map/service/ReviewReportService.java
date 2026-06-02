package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.ReviewReportCreateRequestDto;
import com.tj.GFV_Map.dto.response.ReviewReportResponseDto;
import com.tj.GFV_Map.entity.Review;
import com.tj.GFV_Map.entity.ReviewReport;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.ReportStatus;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.ReviewReportRepository;
import com.tj.GFV_Map.repository.ReviewRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewReportService {

    private final ReviewReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // ===== 신고 등록 (OWNER만) =====
    @Transactional
    public ReviewReportResponseDto createReport(Long userId, ReviewReportCreateRequestDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Review review = reviewRepository.findById(req.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 권한 1: OWNER 역할
        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalStateException("점주만 리뷰를 신고할 수 있습니다.");
        }

        // 권한 2: 본인 식당의 리뷰만 신고 가능
        if (review.getRestaurant().getOwner() == null
                || !review.getRestaurant().getOwner().getId().equals(userId)) {
            throw new IllegalStateException("본인 식당의 리뷰만 신고할 수 있습니다.");
        }

        // 중복 신고 차단
        reportRepository.findByReviewIdAndUserId(req.getReviewId(), userId)
                .ifPresent(r -> {
                    throw new IllegalStateException("이미 신고하신 리뷰입니다.");
                });

        ReviewReport report = ReviewReport.builder()
                .review(review)
                .user(user)
                .category(req.getCategory())
                .detail(req.getDetail())
                .build();
        ReviewReport saved = reportRepository.save(report);

        return ReviewReportResponseDto.from(saved);
    }

    // ===== 관리자: 신고 목록 조회 =====
    public Page<ReviewReportResponseDto> getAllReports(Long adminId, Pageable pageable) {
        verifyAdmin(adminId);
        return reportRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(ReviewReportResponseDto::from);
    }

    // ===== 관리자: 상태별 신고 목록 =====
    public Page<ReviewReportResponseDto> getReportsByStatus(
            Long adminId, ReportStatus status, Pageable pageable) {
        verifyAdmin(adminId);
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(ReviewReportResponseDto::from);
    }

    // ===== 관리자: 신고 인정 → 리뷰 숨김처리 =====
    @Transactional
    public ReviewReportResponseDto resolveReport(Long adminId, Long reportId, String adminNote) {
        User admin = verifyAdmin(adminId);

        ReviewReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new IllegalStateException("이미 처리 완료된 신고입니다.");
        }

        // 1. 신고 처리완료 마크
        report.resolve(admin, adminNote);

        // 2. 리뷰 숨김처리
        report.getReview().hide();

        // 3. 식당 평점 재계산은 Review 자체엔 없으니 여기선 패스
        //    (정확하게 하려면 ReviewService 호출하거나 별도 처리 — 일단 단순화)

        return ReviewReportResponseDto.from(report);
    }

    // ===== Helper: 관리자 권한 체크 =====
    private User verifyAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
        return admin;
    }
}