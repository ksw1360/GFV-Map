package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.response.FavoriteResponseDto;
import com.tj.GFV_Map.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 토글 (별 누르기 = 추가/취소 한 번에)
    @PostMapping("/{restaurantId}")
    public ResponseEntity<Map<String, Object>> toggle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(favoriteService.toggleFavorite(userId, restaurantId));
    }

    // 내 즐겨찾기 목록 (마이페이지)
    @GetMapping("/my")
    public ResponseEntity<Page<FavoriteResponseDto>> getMy(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(userId, pageable));
    }

    // 특정 식당 찜 여부 (프론트가 별 채울지 결정)
    @GetMapping("/check/{restaurantId}")
    public ResponseEntity<Map<String, Boolean>> isFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long restaurantId) {
        boolean fav = favoriteService.isFavorite(userId, restaurantId);
        return ResponseEntity.ok(Map.of("isFavorite", fav));
    }
}
