package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewCreateRequestDto {
    @JsonAlias("restaurant_id")
    private Long restaurantId;
    private BigDecimal rating;          // 1.0 ~ 5.0
    private String content;
    private List<String> photos;        // 사진 URL 배열 (선택)
    private LocalDate visitDate;        // 방문일 (선택)
    private Integer companionCount;     // 동반자 수 (선택)
    private String recommendedMenu;     // 추천 메뉴 (선택)
}
