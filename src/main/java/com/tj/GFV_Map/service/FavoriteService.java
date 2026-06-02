package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.FavoriteResponseDto;
import com.tj.GFV_Map.entity.Favorite;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.repository.FavoriteRepository;
import com.tj.GFV_Map.repository.RestaurantRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /**
     * 토글: 이미 있으면 삭제, 없으면 추가
     * 응답: { added: true/false, favoriteCount: 12 }
     */
    @Transactional
    public Map<String, Object> toggleFavorite(Long userId, Long restaurantId) {
        // 식당 존재 확인
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("존재하지 않는 식당입니다.");
        }

        boolean exists = favoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId);

        if (exists) {
            // 취소
            favoriteRepository.deleteByUserIdAndRestaurantId(userId, restaurantId);
        } else {
            // 추가
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);

            Favorite fav = Favorite.builder()
                    .user(user)
                    .restaurant(restaurant)
                    .build();
            favoriteRepository.save(fav);
        }

        long count = favoriteRepository.countByRestaurantId(restaurantId);

        return Map.of(
                "added", !exists,           // 방금 추가됐으면 true
                "favoriteCount", count       // 현재 총 즐겨찾기 수
        );
    }

    /**
     * 내 즐겨찾기 목록
     */
    public Page<FavoriteResponseDto> getMyFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(FavoriteResponseDto::from);
    }

    /**
     * 특정 식당이 내 즐겨찾기인지 (프론트가 별 채울지 빈 별 띄울지 결정용)
     */
    public boolean isFavorite(Long userId, Long restaurantId) {
        return favoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }
}