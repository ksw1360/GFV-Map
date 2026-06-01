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

    // 상호명 조회
    @GetMapping("/name")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantName(
            @RequestParam(required = false) String keyword
    )
    {
        return ResponseEntity.ok(restaurantService.getAllRestaurant(keyword));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantSearchResponseDto>> search(
            @ModelAttribute RestaurantSearchRequestDto req) {
        return ResponseEntity.ok(restaurantService.search(req));
    }

    @GetMapping("/{id}/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenus(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getMenusByRestaurantId(id));
    }
}
