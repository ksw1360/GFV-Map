package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.RestaurantSearchRequestDto;
import com.tj.GFV_Map.dto.response.MenuResponseDto;
import com.tj.GFV_Map.dto.response.RestaurantResponseDto;
import com.tj.GFV_Map.dto.response.RestaurantSearchResponseDto;
import com.tj.GFV_Map.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    // 상호명 조회 (keyword 없으면 전체)
    @GetMapping("/name")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantName(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(restaurantService.getAllRestaurant(keyword));
    }

    // 지역(주소) 조회 (keyword 없으면 전체)
    @GetMapping("/address")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantAddress(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(restaurantService.getAllAddress(keyword));
    }

    // 내 주변 검색 (radius 단위: 미터, 기본 1km)
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponseDto>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") double radius
    ) {
        return ResponseEntity.ok(restaurantService.searchNearby(lat, lng, radius));
    }

    // 복합 검색 (메뉴 / 채식타입 / 상호명 / 주소 + matchedMenus 반환)
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantSearchResponseDto>> search(
            @ModelAttribute RestaurantSearchRequestDto req
    ) {
        return ResponseEntity.ok(restaurantService.search(req));
    }

    // 특정 가게의 메뉴판 조회
    @GetMapping("/{id}/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenus(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getMenusByRestaurantId(id));
    }
}