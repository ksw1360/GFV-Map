package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.RestaurantResponseDto;
import com.tj.GFV_Map.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // 상호명 검색
    public List<RestaurantResponseDto> getAllRestaurant(String keyword){
        return restaurantRepository.findByNameContaining(keyword).stream().map(RestaurantResponseDto::from).toList();
    }

    // 주소 검색
    public  List<RestaurantResponseDto> getAllAddress(String keyword)
    {
        return restaurantRepository.findByAddressContaining(keyword).stream().map(RestaurantResponseDto::from).toList();
    }

    // 내 주변 검색
    public List<RestaurantResponseDto> searchNearby(double lat, double lng, double radiusMeters) {
        String point = String.format("POINT(%f %f)", lng, lat);
        return restaurantRepository.findNearby(point, radiusMeters)
                .stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }
}
