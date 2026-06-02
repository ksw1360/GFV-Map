package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewUpdateRequestDto {
    private BigDecimal rating;
    private String content;
    private List<String> photos;
    private LocalDate visitDate;
    private Integer companionCount;
    private String recommendedMenu;
    // restaurantId는 필요 없음 (한 사용자 한 식당 한 리뷰니까)
}
