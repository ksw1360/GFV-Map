package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.VeganType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantSearchRequestDto {
    public String name;
    public String address;
    public String menu;
    public VeganType veganType;

    // 검색 모드 선택
    //  true (기본) : 모드 A — 그 메뉴 자체가 해당 채식타입인 식당만
    //  false       : 모드 B — 키워드 메뉴와 채식 메뉴를 따로 보유해도 매칭
    // @ModelAttribute 바인딩이라 쿼리파라미터 ?sameMenu=false 로 끌 수 있음.
    // 값을 안 보내면 setter 가 호출되지 않아 기본 true 유지.
    public Boolean sameMenu = true;
}
