package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.PhotoType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

@Getter
@Setter
@NoArgsConstructor
public class PhotoCreateRequestDto {
    private String url;            // 이미 업로드된 이미지의 URL (필수)
    private PhotoType type;        // RESTAURANT(가게 사진) / MENU(메뉴 사진) (필수)
    @JsonAlias("restaurant_id")
    private Long restaurantId;     // type=RESTAURANT일 때 필수
    private Long menuId;           // type=MENU일 때 필수
    private String caption;        // 사진 설명 (선택)
    private Boolean isMain;        // 대표 사진 지정 (선택)
}
