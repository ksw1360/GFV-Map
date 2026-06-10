package com.tj.GFV_Map.dto.response;

import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.PriceRange;
import com.tj.GFV_Map.enums.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RestaurantResponseDto {
    private Long restaurant_id;
    private String name;
    private String address;
    private String addressDetail;
//    private Point location;
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
//    private User owner;

    public static RestaurantResponseDto from(Restaurant restaurant) {
        return new RestaurantResponseDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getAddressDetail(),
//                restaurant.getLocation(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getPhone(),
                restaurant.getCategory(),
                restaurant.getSubCategory(),
                restaurant.getPriceRange(),
                restaurant.getBusinessHours(),
                restaurant.getHolidays(),
                restaurant.getLastOrderTime(),
                restaurant.getHasParking(),
                restaurant.getHasRoom(),
                restaurant.getHasDelivery(),
                restaurant.getHasReservation(),
                restaurant.getPaymentMethods(),
                restaurant.getAmenities(),
                restaurant.getTags(),
                restaurant.getAtmosphere(),
                restaurant.getSnsLinks(),
                restaurant.getStatus(),
                restaurant.getIsVerified()
//                restaurant.getOwner()
        );
    }
}
