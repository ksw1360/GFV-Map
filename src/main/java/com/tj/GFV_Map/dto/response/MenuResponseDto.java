package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.enums.MenuCategory;
import com.tj.GFV_Map.enums.VeganType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)   // null 필드는 응답에서 빼기 (응답 깔끔하게)
public class MenuResponseDto {
    private Long menuId;
    private String name;
    private Integer price;
    private String description;
    private MenuCategory category;       // 메인/사이드/음료/디저트
    private VeganType veganType;         // 비건/락토/오보/락토오보/페스코
    private List<String> allergens;      // ["땅콩", "글루텐"]
    private String imageUrl;
    private Boolean isSignature;
    private Boolean isAvailable;
    private Boolean isSeasonal;

    public static MenuResponseDto from(Menu menu) {
        return MenuResponseDto.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .category(menu.getCategory())
                .veganType(menu.getVeganType())
                .allergens(menu.getAllergens())
                .imageUrl(menu.getImageUrl())
                .isSignature(menu.getIsSignature())
                .isAvailable(menu.getIsAvailable())
                .isSeasonal(menu.getIsSeasonal())
                .build();
    }
}