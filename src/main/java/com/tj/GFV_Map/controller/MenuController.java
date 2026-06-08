package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.MenuCreateRequestDto;
import com.tj.GFV_Map.dto.request.MenuUpdateRequestDto;
import com.tj.GFV_Map.dto.response.MenuResponseDto;
import com.tj.GFV_Map.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 메뉴 추가 (점주) — restaurantId는 body로
    @PostMapping
    public ResponseEntity<MenuResponseDto> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody MenuCreateRequestDto req) {
        return ResponseEntity.ok(menuService.createMenu(userId, req));
    }

    // 메뉴 수정 (점주)
    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long menuId,
            @RequestBody MenuUpdateRequestDto req) {
        return ResponseEntity.ok(menuService.updateMenu(userId, menuId, req));
    }

    // 메뉴 삭제 (점주)
    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long menuId) {
        menuService.deleteMenu(userId, menuId);
        return ResponseEntity.ok("메뉴가 삭제되었습니다.");
    }
}