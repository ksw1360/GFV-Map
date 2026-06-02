package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository  extends JpaRepository<Favorite, Favorite.FavoriteId> {
    // 토글용: 존재 여부 확인
    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    // 삭제 (토글 취소용)
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId AND f.restaurant.id = :restaurantId")
    void deleteByUserIdAndRestaurantId(@Param("userId") Long userId,
                                       @Param("restaurantId") Long restaurantId);

    // 내 즐겨찾기 목록 (페이징, 최신순)
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 식당의 즐겨찾기 수 (집계용)
    long countByRestaurantId(Long restaurantId);
}
