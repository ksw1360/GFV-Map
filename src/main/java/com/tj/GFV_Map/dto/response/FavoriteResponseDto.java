package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Favorite;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteResponseDto {
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String points;          // "lat/lng"
    private Double avgRating;
    private Integer reviewCount;
    private LocalDateTime favoritedAt;

    public static FavoriteResponseDto from(Favorite fav) {
        var r = fav.getRestaurant();
        return FavoriteResponseDto.builder()
                .restaurantId(r.getId())
                .restaurantName(r.getName())
                .restaurantAddress(r.getAddress()
                        + (r.getAddressDetail() != null ? r.getAddressDetail() : ""))
                .points(r.getLatitude() + "/" + r.getLongitude())
                .avgRating(r.getAvgRating())
                .reviewCount(r.getReviewCount())
                .favoritedAt(fav.getCreatedAt())
                .build();
    }
}