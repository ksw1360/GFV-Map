package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.RestaurantUpdateRequestDto;
import com.tj.GFV_Map.dto.response.RestaurantDetailResponseDto;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantOwnerService {

    private final RestaurantRepository restaurantRepository;

    // ===== 가게 상세 조회 (공개) =====
    public RestaurantDetailResponseDto getDetail(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));
        return RestaurantDetailResponseDto.from(restaurant);
    }

    // ===== 내 가게 목록 (점주) =====
    public List<RestaurantDetailResponseDto> getMyRestaurants(Long userId) {
        return restaurantRepository.findByOwnerId(userId).stream()
                .map(RestaurantDetailResponseDto::from)
                .toList();
    }

    // ===== 가게 수정 (점주 본인만) =====
    @Transactional
    public RestaurantDetailResponseDto updateRestaurant(Long userId, Long restaurantId,
                                                        RestaurantUpdateRequestDto req) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        if (restaurant.getOwner() == null
                || !restaurant.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("내 가게만 수정할 수 있습니다.");
        }

        restaurant.updateInfo(
                req.getName(),
                req.getAddress(),
                req.getAddressDetail(),
                req.getPhone(),
                req.getCategory(),
                req.getSubCategory(),
                req.getPriceRange(),
                req.getBusinessHours(),
                req.getHolidays(),
                req.getLastOrderTime(),
                req.getHasParking(),
                req.getHasRoom(),
                req.getHasDelivery(),
                req.getHasReservation(),
                req.getPaymentMethods(),
                req.getAmenities(),
                req.getTags(),
                req.getAtmosphere(),
                req.getSnsLinks(),
                req.getStatus()
        );

        return RestaurantDetailResponseDto.from(restaurant);
    }
}