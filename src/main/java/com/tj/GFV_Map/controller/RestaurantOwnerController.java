package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.RestaurantUpdateRequestDto;
import com.tj.GFV_Map.dto.response.RestaurantDetailResponseDto;
import com.tj.GFV_Map.service.RestaurantOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantOwnerController {

    private final RestaurantOwnerService restaurantOwnerService;

    // 내 가게 목록 (점주) — '/{id}'보다 먼저, 리터럴이라 충돌 없음
    @GetMapping("/my")
    public ResponseEntity<List<RestaurantDetailResponseDto>> getMyRestaurants(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(restaurantOwnerService.getMyRestaurants(userId));
    }

    // 가게 상세 조회 (공개)
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetailResponseDto> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantOwnerService.getDetail(id));
    }

    // 가게 수정 (점주 본인만) — 보낸 필드만 반영
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDetailResponseDto> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @RequestBody RestaurantUpdateRequestDto req) {
        return ResponseEntity.ok(restaurantOwnerService.updateRestaurant(userId, id, req));
    }
}