package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDto {
    private Long reviewId;
    private Long restaurantId;
    private String restaurantName;
    private Long userId;
    private String userNickname;
    private String userProfileImageUrl;
    private BigDecimal rating;
    private String content;
    private List<String> photos;
    private LocalDate visitDate;
    private Integer companionCount;
    private String recommendedMenu;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .restaurantId(review.getRestaurant().getId())
                .restaurantName(review.getRestaurant().getName())
                .userId(review.getUser().getId())
                .userNickname(review.getUser().getNickname())
                .userProfileImageUrl(review.getUser().getProfileImageUrl())
                .rating(review.getRating())
                .content(review.getContent())
                .photos(review.getPhotos())
                .visitDate(review.getVisitDate())
                .companionCount(review.getCompanionCount())
                .recommendedMenu(review.getRecommendedMenu())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}