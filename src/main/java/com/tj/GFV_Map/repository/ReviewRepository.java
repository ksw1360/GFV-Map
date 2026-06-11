package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 한 사용자가 한 식당에 쓴 리뷰 (수정/중복 체크용)
    Optional<Review> findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    // 식당의 리뷰 목록 (신고숨김 X + 본인삭제 X, 최신순, 페이징)
    Page<Review> findByRestaurantIdAndIsHiddenFalseAndIsDeletedFalseOrderByCreatedAtDesc(
            Long restaurantId, Pageable pageable);

    // 점주용: 식당의 리뷰 목록 (숨김 포함, 본인삭제 X, 최신순) — 신고된 리뷰도 표시하기 위함
    Page<Review> findByRestaurantIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long restaurantId, Pageable pageable);

    // 사용자가 쓴 리뷰 목록 (마이페이지용)
    Page<Review> findByUserIdAndIsHiddenFalseAndIsDeletedFalseOrderByCreatedAtDesc(
            Long userId, Pageable pageable);

    // 식당의 평균 평점 계산 (신고숨김 X + 본인삭제 X)
    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.restaurant.id = :restaurantId " +
            "AND r.isHidden = false AND r.isDeleted = false")
    BigDecimal findAvgRatingByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 식당의 리뷰 개수 (신고숨김 X + 본인삭제 X)
    long countByRestaurantIdAndIsHiddenFalseAndIsDeletedFalse(Long restaurantId);
}