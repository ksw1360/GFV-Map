package com.tj.GFV_Map.enums;

public enum ReportStatus {
    PENDING("대기중"),
    IN_PROGRESS("처리중"),
    RESOLVED("신고 완료"),
    REJECTED("해제 완료");

    private final String label;

    ReportStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}