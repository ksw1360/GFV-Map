package com.tj.GFV_Map.controller;



import com.tj.GFV_Map.dto.response.RestaurantResponseDto;
import com.tj.GFV_Map.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    // 상호명 조회
    @GetMapping("/name")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantName(
            @RequestParam(required = false) String keyword
    )
    {
        return ResponseEntity.ok(restaurantService.getAllRestaurant(keyword));
    }
}
