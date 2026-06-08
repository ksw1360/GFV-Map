package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.enums.PriceRange;
import com.tj.GFV_Map.enums.RestaurantStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDetailResponseDto {
    private Long restaurantId;          // 기존 RestaurantResponseDto엔 없던 id 포함 (프론트가 메뉴/수정 호출에 필요)
    private String name;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String category;
    private String subCategory;
    private PriceRange priceRange;
    private Map<String, Object> businessHours;
    private String holidays;
    private LocalTime lastOrderTime;
    private Boolean hasParking;
    private Boolean hasRoom;
    private Boolean hasDelivery;
    private Boolean hasReservation;
    private List<String> paymentMethods;
    private List<String> amenities;
    private List<String> tags;
    private List<String> atmosphere;
    private Map<String, String> snsLinks;
    private RestaurantStatus status;
    private Boolean isVerified;
    private Double avgRating;
    private Integer reviewCount;

    public static RestaurantDetailResponseDto from(Restaurant r) {
        return RestaurantDetailResponseDto.builder()
                .restaurantId(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .addressDetail(r.getAddressDetail())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .phone(r.getPhone())
                .category(r.getCategory())
                .subCategory(r.getSubCategory())
                .priceRange(r.getPriceRange())
                .businessHours(r.getBusinessHours())
                .holidays(r.getHolidays())
                .lastOrderTime(r.getLastOrderTime())
                .hasParking(r.getHasParking())
                .hasRoom(r.getHasRoom())
                .hasDelivery(r.getHasDelivery())
                .hasReservation(r.getHasReservation())
                .paymentMethods(r.getPaymentMethods())
                .amenities(r.getAmenities())
                .tags(r.getTags())
                .atmosphere(r.getAtmosphere())
                .snsLinks(r.getSnsLinks())
                .status(r.getStatus())
                .isVerified(r.getIsVerified())
                .avgRating(r.getAvgRating())
                .reviewCount(r.getReviewCount())
                .build();
    }
}