package com.tj.GFV_Map.enums;

public enum MenuCategory {
    MAIN("메인"),
    SIDE("사이드"),
    DRINK("음료"),
    DESSERT("디저트");

    private final String label;

    MenuCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}