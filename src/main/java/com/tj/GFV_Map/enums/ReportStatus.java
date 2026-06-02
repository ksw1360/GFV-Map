package com.tj.GFV_Map.enums;

public enum ReportStatus {
    PENDING("대기중"),
    IN_PROGRESS("처리중"),
    RESOLVED("처리완료");

    private final String label;

    ReportStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
