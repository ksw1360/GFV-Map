package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.ReviewReport;
import com.tj.GFV_Map.enums.ReportCategory;
import com.tj.GFV_Map.enums.ReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewReportResponseDto {
    private Long reportId;
    private Long reviewId;
    private String reviewContent;       // 신고된 리뷰 본문 (관리자 화면용)
    private Long reporterId;
    private String reporterNickname;
    private ReportCategory category;
    private String categoryLabel;       // "욕설/비방" 같은 한글
    private String detail;
    private ReportStatus status;
    private String statusLabel;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public static ReviewReportResponseDto from(ReviewReport report) {
        return ReviewReportResponseDto.builder()
                .reportId(report.getId())
                .reviewId(report.getReview().getId())
                .reviewContent(report.getReview().getContent())
                .reporterId(report.getUser().getId())
                .reporterNickname(report.getUser().getNickname())
                .category(report.getCategory())
                .categoryLabel(report.getCategory().getLabel())
                .detail(report.getDetail())
                .status(report.getStatus())
                .statusLabel(report.getStatus().getLabel())
                .adminNote(report.getAdminNote())
                .createdAt(report.getCreatedAt())
                .resolvedAt(report.getResolvedAt())
                .build();
    }
}