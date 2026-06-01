package com.tj.GFV_Map.enums;

public enum VeganType {
    VEGAN("비건"),         // 동물성 식품 일체 X
    LACTO("락토"),         // 유제품 O
    OVO("오보"),           // 계란 O
    LACTO_OVO("락토오보"), // 유제품 + 계란 O
    PESCO("페스코");       // 생선 O

    private final String label;

    VeganType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}