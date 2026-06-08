package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.PriceRange;
import com.tj.GFV_Map.enums.RestaurantStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantUpdateRequestDto {
    // 보낸 필드만 반영됨 (null은 무시 = 기존 값 유지)
    private String name;
    private String address;
    private String addressDetail;
    private String phone;
    private String category;
    private String subCategory;
    private PriceRange priceRange;             // LOW / MEDIUM / HIGH / VERY_HIGH
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
    private RestaurantStatus status;           // OPEN / BREAK_TIME / CLOSED

    // 주의: 위경도/좌표(location)는 지오코딩이 필요해 수정 대상에서 제외함.
    //       avgRating·reviewCount·isVerified·owner는 시스템/관리자 영역이라 제외.
}