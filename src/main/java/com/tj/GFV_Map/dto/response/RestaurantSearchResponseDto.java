package com.tj.GFV_Map.dto.response;

import com.tj.GFV_Map.enums.VeganType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class RestaurantSearchResponseDto {
    private Long restaurantId;
    private String name;
    private String address;        // address + addressDetail 합친 거
    private String points;         // "latitude/longitude"
    private List<String> matchedMenus;  // 매칭된 메뉴들 (검색어 있을 때만)
    private VeganType veganType;   // 👈 추가
}
