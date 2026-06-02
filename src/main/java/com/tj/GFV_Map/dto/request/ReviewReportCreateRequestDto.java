package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.ReportCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewReportCreateRequestDto {
    private Long reviewId;
    private ReportCategory category;   // ABUSE / FALSE_REVIEW / AD / IRRELEVANT
    private String detail;             // 선택
}