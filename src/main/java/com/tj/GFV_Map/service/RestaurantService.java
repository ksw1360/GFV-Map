package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.RestaurantSearchRequestDto;
import com.tj.GFV_Map.dto.response.MenuResponseDto;
import com.tj.GFV_Map.dto.response.RestaurantResponseDto;
import com.tj.GFV_Map.dto.response.RestaurantSearchResponseDto;
import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.repository.MenuRepository;
import com.tj.GFV_Map.repository.RestaurantRepository;
import com.tj.GFV_Map.repository.RestaurantSearchSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tj.GFV_Map.enums.VeganType;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

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

    @Transactional(readOnly = false)
    public List<RestaurantSearchResponseDto> search(RestaurantSearchRequestDto req) {
        // Specification 동적 조립 — null 인 조건은 알아서 빠짐
        Specification<Restaurant> spec = Specification
                .where(RestaurantSearchSpec.nameContains(req.getName()))
                .and(RestaurantSearchSpec.addressContains(req.getAddress()))
                .and(RestaurantSearchSpec.hasMenuContaining(req.getMenu()))
                .and(RestaurantSearchSpec.hasMenuWithVeganType(req.getVeganType()));  // 👈 추가;
        List<Restaurant> restaurants = restaurantRepository.findAll(spec);

        // 메뉴 검색어가 있으면, 각 식당에서 그 키워드 매칭된 메뉴만 추출
        String menuKeyword = req.getMenu();

        return restaurants.stream()
                .map(r -> {
                    List<String> matchedMenus = (menuKeyword == null || menuKeyword.isBlank())
                            ? List.of()
                            : r.getMenus().stream()
                              .filter(m -> m.getName().contains(menuKeyword))
                              .map(Menu::getName)
                              .toList();

                    String address = r.getAddress()
                            + (r.getAddressDetail() != null ? r.getAddressDetail() : "");
                    String points = r.getLatitude() + "/" + r.getLongitude();

                    return RestaurantSearchResponseDto.builder()
                            .restaurantId(r.getId())
                            .name(r.getName())
                            .address(address)
                            .points(points)
                            .matchedMenus(matchedMenus)
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MenuResponseDto> getMenusByRestaurantId(Long restaurantId) {
        // 식당 존재 확인 (없는 ID 로 호출 시 친절한 에러)
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("존재하지 않는 식당입니다.");
        }

        return menuRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId)
                .stream()
                .map(MenuResponseDto::from)
                .toList();
    }
}
