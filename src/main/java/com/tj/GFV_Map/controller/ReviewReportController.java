package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.ReviewReportCreateRequestDto;
import com.tj.GFV_Map.dto.response.ReviewReportResponseDto;
import com.tj.GFV_Map.enums.ReportStatus;
import com.tj.GFV_Map.service.ReviewReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review-report")
@RequiredArgsConstructor
public class ReviewReportController {

    private final ReviewReportService reportService;

    // 신고 등록 (OWNER)
    @PostMapping
    public ResponseEntity<ReviewReportResponseDto> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewReportCreateRequestDto req) {
        return ResponseEntity.ok(reportService.createReport(userId, req));
    }

    // 관리자: 전체 신고 조회 (페이징)
    @GetMapping("/admin")
    public ResponseEntity<Page<ReviewReportResponseDto>> getAll(
            @AuthenticationPrincipal Long adminId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllReports(adminId, pageable));
    }

    // 관리자: 상태별 신고 조회 (예: ?status=PENDING)
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<Page<ReviewReportResponseDto>> getByStatus(
            @AuthenticationPrincipal Long adminId,
            @PathVariable ReportStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reportService.getReportsByStatus(adminId, status, pageable));
    }

    // 관리자: 신고 인정 → 리뷰 숨김
    @PostMapping("/admin/{reportId}/resolve")
    public ResponseEntity<ReviewReportResponseDto> resolve(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long reportId,
            @RequestBody(required = false) Map<String, String> body) {
        String adminNote = body != null ? body.get("adminNote") : null;
        return ResponseEntity.ok(reportService.resolveReport(adminId, reportId, adminNote));
    }
}