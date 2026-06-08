package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.MenuCategory;
import com.tj.GFV_Map.enums.VeganType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MenuUpdateRequestDto {
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private MenuCategory category;
    private VeganType veganType;
    private List<String> allergens;
    private Boolean isAvailable;        // 판매중 여부 토글 (선택)
}