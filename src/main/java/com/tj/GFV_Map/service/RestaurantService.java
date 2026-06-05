package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.RestaurantSearchRequestDto;
import com.tj.GFV_Map.dto.response.MenuResponseDto;
import com.tj.GFV_Map.dto.response.RestaurantResponseDto;
import com.tj.GFV_Map.dto.response.RestaurantSearchResponseDto;
import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.enums.VeganType;
import com.tj.GFV_Map.repository.MenuRepository;
import com.tj.GFV_Map.repository.RestaurantRepository;
import com.tj.GFV_Map.repository.RestaurantSearchSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    // 상호명 검색 (키워드 없으면 전체 반환)
    public List<RestaurantResponseDto> getAllRestaurant(String keyword) {
        List<Restaurant> list = (keyword == null || keyword.isBlank())
                ? restaurantRepository.findAll()
                : restaurantRepository.findByNameContaining(keyword);
        return list.stream().map(RestaurantResponseDto::from).toList();
    }

    // 주소 검색 (키워드 없으면 전체 반환)
    public List<RestaurantResponseDto> getAllAddress(String keyword) {
        List<Restaurant> list = (keyword == null || keyword.isBlank())
                ? restaurantRepository.findAll()
                : restaurantRepository.findByAddressContaining(keyword);
        return list.stream().map(RestaurantResponseDto::from).toList();
    }

    // 내 주변 검색
    public List<RestaurantResponseDto> searchNearby(double lat, double lng, double radiusMeters) {
        String point = String.format("POINT(%f %f)", lng, lat);
        return restaurantRepository.findNearby(point, radiusMeters)
                .stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    public List<RestaurantSearchResponseDto> search(RestaurantSearchRequestDto req) {
        String keyword = req.getMenu();
        VeganType veganType = req.getVeganType();
        boolean sameMenu = (req.getSameMenu() == null) || req.getSameMenu(); // 기본 true (모드 A)

        Specification<Restaurant> spec = Specification
                .where(RestaurantSearchSpec.nameContains(req.getName()))
                .and(RestaurantSearchSpec.addressContains(req.getAddress()));

        if (sameMenu) {
            // 모드 A: 같은 메뉴 한 줄에 키워드 + 채식타입 (JOIN 1번)
            spec = spec.and(RestaurantSearchSpec.menuSameRow(keyword, veganType));
        } else {
            // 모드 B: 키워드 메뉴와 채식 메뉴를 각각 보유 (JOIN 2번)
            spec = spec.and(RestaurantSearchSpec.hasMenuContaining(keyword))
                       .and(RestaurantSearchSpec.hasMenuWithVeganType(veganType));
        }

        List<Restaurant> restaurants = restaurantRepository.findAll(spec);

        String kwLower = (keyword != null && !keyword.isBlank()) ? keyword.toLowerCase() : null;

        return restaurants.stream()
                .map(r -> {
                    // 이 식당이 왜 잡혔는지 보여줄 메뉴들
                    List<String> matchedMenus = r.getMenus().stream()
                            .filter(m -> menuMatches(m, kwLower, veganType, sameMenu))
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

    // matchedMenus 판정. SQL LIKE 와 대소문자 기준을 맞추기 위해 toLowerCase 비교.
    private boolean menuMatches(Menu m, String kwLower, VeganType veganType, boolean sameMenu) {
        if (m.getName() == null) return false;

        boolean kwGiven = kwLower != null;
        boolean vtGiven = veganType != null;
        if (!kwGiven && !vtGiven) return false; // 메뉴 조건 자체가 없으면 강조할 것 없음

        boolean kwHit = kwGiven && m.getName().toLowerCase().contains(kwLower);
        boolean vtHit = vtGiven && veganType.equals(m.getVeganType());

        if (sameMenu) {
            // 모드 A: 같은 메뉴가 주어진 조건을 모두 만족해야 강조
            return (!kwGiven || kwHit) && (!vtGiven || vtHit);
        } else {
            // 모드 B: 키워드 메뉴 또는 채식타입 메뉴면 강조
            return kwHit || vtHit;
        }
    }

    @Transactional(readOnly = true)
    public List<MenuResponseDto> getMenusByRestaurantId(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("존재하지 않는 식당입니다.");
        }
        return menuRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId)
                .stream()
                .map(MenuResponseDto::from)
                .toList();
    }
}
