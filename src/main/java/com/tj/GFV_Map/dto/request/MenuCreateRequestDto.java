package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.MenuCategory;
import com.tj.GFV_Map.enums.VeganType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MenuCreateRequestDto {
    @JsonAlias("restaurant_id")
    private Long restaurantId;          // 어느 가게의 메뉴인지 (필수)
    private String name;                // 메뉴명 (필수)
    private Integer price;              // 가격
    private String description;         // 설명
    private MenuCategory category;      // MAIN / SIDE / DRINK / DESSERT
    private VeganType veganType;        // VEGAN / LACTO / OVO / LACTO_OVO / PESCO
    private List<String> allergens;     // ["땅콩", "글루텐"]
    private String imageUrl;            // 메뉴 사진 URL (선택)
    private Boolean isSignature;        // 대표 메뉴 여부
    private Boolean isVegan;            // 비건 여부
    private Boolean isGlutenFree;       // 글루텐프리 여부
    private Integer displayOrder;       // 정렬 순서
}