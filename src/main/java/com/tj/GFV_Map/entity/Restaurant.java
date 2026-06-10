package com.tj.GFV_Map.entity;


import com.tj.GFV_Map.enums.PriceRange;
import com.tj.GFV_Map.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurantid")
    private Long id;

    @Column(name = "restaurant_name", nullable = false, length = 255)
    private String name;

    @Column(name = "restaurant_address", nullable = false, length = 255)
    private String address;

    @Column(name = "restaurant_address_detail", length = 255)
    private String addressDetail;

    @Column(name = "restaurant_location", nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(name = "restaurant_latitude", nullable = false)
    private Double latitude;

    @Column(name = "restaurant_longitude", nullable = false)
    private Double longitude;

    @Column(name = "restaurant_phone", length = 50)
    private String phone;

    @Column(name = "restaurant_category", nullable = false, length = 50)
    private String category;

    @Column(name = "restaurant_sub_category", length = 100)
    private String subCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "restaurant_price_range", nullable = false, length = 50)
    private PriceRange priceRange;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "restaurant_business_hours", nullable = false, columnDefinition = "json")
    private Map<String, Object> businessHours;

    @Column(name = "restaurant_holidays", length = 255)
    private String holidays;

    @Column(name = "restaurant_last_order_time")
    private LocalTime lastOrderTime;

    @Column(name = "restaurant_avg_rating")
    private Double avgRating = 0.0;

    @Column(name = "restaurant_review_count")
    private Integer reviewCount = 0;

    @Column(name = "restaurant_has_parking")
    private Boolean hasParking = false;

    @Column(name = "restaurant_has_room")
    private Boolean hasRoom = false;

    @Column(name = "restaurant_has_delivery")
    private Boolean hasDelivery = false;

    @Column(name = "restaurant_has_reservation")
    private Boolean hasReservation = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "restaurant_payment_methods", columnDefinition = "json")
    private List<String> paymentMethods;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "restaurant_amenities", columnDefinition = "json")
    private List<String> amenities;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "restaurant_tags", columnDefinition = "json")
    private List<String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "restaurant_atmosphere", columnDefinition = "json")
    private List<String> atmosphere;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "restaurant_sns_links", columnDefinition = "json")
    private Map<String, String> snsLinks;

    @Enumerated(EnumType.STRING)
    @Column(name = "restaurant_status", nullable = false, length = 20)
    private RestaurantStatus status;

    @Column(name = "restaurant_is_verified")
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_owner_id")
    private User owner;

    @CreationTimestamp
    @Column(name = "restaurant_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "restaurant_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Restaurant(String name,
                       String address,
                       String addressDetail,
                       Point location,
                       Double latitude,
                       Double longitude,
                       String phone,
                       String category,
                       String subCategory,
                       PriceRange priceRange,
                       Map<String, Object> businessHours,
                       String holidays,
                       LocalTime lastOrderTime,
                       Boolean hasParking,
                       Boolean hasRoom,
                       Boolean hasDelivery,
                       Boolean hasReservation,
                       List<String> paymentMethods,
                       List<String> amenities,
                       List<String> tags,
                       List<String> atmosphere,
                       Map<String, String> snsLinks,
                       RestaurantStatus status,
                       Boolean isVerified,
                       User owner) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.category = category;
        this.subCategory = subCategory;
        this.priceRange = priceRange;
        this.businessHours = businessHours;
        this.holidays = holidays;
        this.lastOrderTime = lastOrderTime;
        this.hasParking = hasParking != null ? hasParking : false;
        this.hasRoom = hasRoom != null ? hasRoom : false;
        this.hasDelivery = hasDelivery != null ? hasDelivery : false;
        this.hasReservation = hasReservation != null ? hasReservation : false;
        this.paymentMethods = paymentMethods;
        this.amenities = amenities;
        this.tags = tags;
        this.atmosphere = atmosphere;
        this.snsLinks = snsLinks;
        this.status = status;
        this.isVerified = isVerified != null ? isVerified : false;
        this.owner = owner;
    }

    public void updateRatingStats(double avgRating, int reviewCount) {
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    public void changeStatus(RestaurantStatus status) {
        this.status = status;
    }

    public void verify() {
        this.isVerified = true;
    }

    //    @OneToMany(mappedBy = "restaurant") private List<Menu> menus;
    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();

    // ============================================================
// [붙여넣을 위치] Restaurant.java 안, 기존 verify() 메서드 아래
// (이미 import 되어 있는 PriceRange, RestaurantStatus, LocalTime, Map, List 그대로 사용 — 새 import 불필요)
// ============================================================

    public void updateInfo(String name,
                           String address,
                           String addressDetail,
                           String phone,
                           String category,
                           String subCategory,
                           PriceRange priceRange,
                           Map<String, Object> businessHours,
                           String holidays,
                           LocalTime lastOrderTime,
                           Boolean hasParking,
                           Boolean hasRoom,
                           Boolean hasDelivery,
                           Boolean hasReservation,
                           List<String> paymentMethods,
                           List<String> amenities,
                           List<String> tags,
                           List<String> atmosphere,
                           Map<String, String> snsLinks,
                           RestaurantStatus status) {
        // null로 들어온 필드는 무시 = 기존 값 유지 (부분 수정)
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (addressDetail != null) this.addressDetail = addressDetail;
        if (phone != null) this.phone = phone;
        if (category != null) this.category = category;
        if (subCategory != null) this.subCategory = subCategory;
        if (priceRange != null) this.priceRange = priceRange;
        if (businessHours != null) this.businessHours = businessHours;
        if (holidays != null) this.holidays = holidays;
        if (lastOrderTime != null) this.lastOrderTime = lastOrderTime;
        if (hasParking != null) this.hasParking = hasParking;
        if (hasRoom != null) this.hasRoom = hasRoom;
        if (hasDelivery != null) this.hasDelivery = hasDelivery;
        if (hasReservation != null) this.hasReservation = hasReservation;
        if (paymentMethods != null) this.paymentMethods = paymentMethods;
        if (amenities != null) this.amenities = amenities;
        if (tags != null) this.tags = tags;
        if (atmosphere != null) this.atmosphere = atmosphere;
        if (snsLinks != null) this.snsLinks = snsLinks;
        if (status != null) this.status = status;
    }
}