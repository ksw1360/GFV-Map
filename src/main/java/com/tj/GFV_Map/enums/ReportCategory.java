package com.tj.GFV_Map.enums;

public enum ReportCategory {
    ABUSE("욕설/비방"),
    SEXUAL("음담패설"),
    FALSE_REVIEW("허위리뷰"),
    AD("광고/홍보"),
    IRRELEVANT("무관한내용");

    private final String label;

    ReportCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
