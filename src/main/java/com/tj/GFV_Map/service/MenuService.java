package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.MenuCreateRequestDto;
import com.tj.GFV_Map.dto.request.MenuUpdateRequestDto;
import com.tj.GFV_Map.dto.response.MenuResponseDto;
import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.repository.MenuRepository;
import com.tj.GFV_Map.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    // ===== 메뉴 추가 =====
    @Transactional
    public MenuResponseDto createMenu(Long userId, MenuCreateRequestDto req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다.");
        }

        // 내 가게인지 검증
        Restaurant restaurant = getOwnedRestaurant(req.getRestaurantId(), userId);

        // 빌더가 실제로 대입하는 필드만 빌더로 세팅
        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .isSignature(req.getIsSignature())
                .isVegan(req.getIsVegan())
                .isGlutenFree(req.getIsGlutenFree())
                .imageUrl(req.getImageUrl())
                .displayOrder(req.getDisplayOrder())
                .build();

        // 빌더 본문에서 대입 안 되는 category/veganType/allergens는 메서드로 세팅
        menu.updateCategoryAndVegan(req.getCategory(), req.getVeganType(), req.getAllergens());

        Menu saved = menuRepository.save(menu);
        return MenuResponseDto.from(saved);
    }

    // ===== 메뉴 수정 =====
    @Transactional
    public MenuResponseDto updateMenu(Long userId, Long menuId, MenuUpdateRequestDto req) {
        Menu menu = getOwnedMenu(menuId, userId);

        // 기본 정보 (name/description/price/imageUrl)
        menu.update(req.getName(), req.getDescription(), req.getPrice(), req.getImageUrl());

        // 분류/비건/알러지
        menu.updateCategoryAndVegan(req.getCategory(), req.getVeganType(), req.getAllergens());

        // 판매 상태 (보낸 경우에만)
        if (req.getIsAvailable() != null) {
            if (req.getIsAvailable()) {
                menu.markAvailable();
            } else {
                menu.markUnavailable();
            }
        }

        return MenuResponseDto.from(menu);
    }

    // ===== 메뉴 삭제 =====
    @Transactional
    public void deleteMenu(Long userId, Long menuId) {
        Menu menu = getOwnedMenu(menuId, userId);
        menuRepository.delete(menu);
    }

    // ===== 소유자 검증 =====
    private Restaurant getOwnedRestaurant(Long restaurantId, Long userId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("restaurantId는 필수입니다.");
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));
        verifyOwner(restaurant, userId);
        return restaurant;
    }

    private Menu getOwnedMenu(Long menuId, Long userId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        verifyOwner(menu.getRestaurant(), userId);
        return menu;
    }

    private void verifyOwner(Restaurant restaurant, Long userId) {
        if (restaurant == null
                || restaurant.getOwner() == null
                || !restaurant.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("내 가게의 메뉴만 관리할 수 있습니다.");
        }
    }
}