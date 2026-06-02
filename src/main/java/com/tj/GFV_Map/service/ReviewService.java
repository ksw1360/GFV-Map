package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.ReviewCreateRequestDto;
import com.tj.GFV_Map.dto.request.ReviewUpdateRequestDto;
import com.tj.GFV_Map.dto.response.ReviewResponseDto;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.entity.Review;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.repository.RestaurantRepository;
import com.tj.GFV_Map.repository.ReviewRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // ===== 리뷰 작성 =====
    @Transactional
    public ReviewResponseDto createReview(Long userId, ReviewCreateRequestDto req) {
        // 1. 별점 검증
        validateRating(req.getRating());

        // 2. 사용자 + 이메일 인증 체크
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!user.getIsEmailVerified()) {
            throw new IllegalStateException("이메일 인증 후 리뷰를 작성할 수 있습니다.");
        }

        // 3. 식당 존재 확인
        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        // 4. 중복 체크 (DB 제약도 있지만 친절한 메시지 위해)
        reviewRepository.findByUserIdAndRestaurantId(userId, req.getRestaurantId())
                .ifPresent(r -> {
                    throw new IllegalStateException(
                            "이미 이 식당에 리뷰를 작성하셨습니다. 수정하시려면 PUT을 사용해주세요.");
                });

        // 5. 저장
        Review review = Review.builder()
                .restaurant(restaurant)
                .user(user)
                .rating(req.getRating())
                .content(req.getContent())
                .photos(req.getPhotos())
                .visitDate(req.getVisitDate())
                .companionCount(req.getCompanionCount())
                .recommendedMenu(req.getRecommendedMenu())
                .build();
        Review saved = reviewRepository.save(review);

        // 6. 식당 평점 재계산
        updateRestaurantRatingStats(restaurant);

        return ReviewResponseDto.from(saved);
    }

    // ===== 리뷰 수정 =====
    @Transactional
    public ReviewResponseDto updateReview(Long userId, Long reviewId, ReviewUpdateRequestDto req) {
        validateRating(req.getRating());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 본인 리뷰만 수정 가능
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 리뷰만 수정할 수 있습니다.");
        }

        review.update(
                req.getRating(),
                req.getContent(),
                req.getPhotos(),
                req.getVisitDate(),
                req.getCompanionCount(),
                req.getRecommendedMenu()
        );

        // 평점 재계산
        updateRestaurantRatingStats(review.getRestaurant());

        return ReviewResponseDto.from(review);
    }

    // ===== 리뷰 삭제 (soft delete) =====
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        review.hide();

        // 평점 재계산 (숨김 처리된 리뷰는 평점에서 제외되므로)
        updateRestaurantRatingStats(review.getRestaurant());
    }

    // ===== 식당의 리뷰 목록 =====
    public Page<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        return reviewRepository
                .findByRestaurantIdAndIsHiddenFalseOrderByCreatedAtDesc(restaurantId, pageable)
                .map(ReviewResponseDto::from);
    }

    // ===== 사용자의 리뷰 목록 (마이페이지) =====
    public Page<ReviewResponseDto> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository
                .findByUserIdAndIsHiddenFalseOrderByCreatedAtDesc(userId, pageable)
                .map(ReviewResponseDto::from);
    }

    // ===== Helper: 별점 검증 =====
    private void validateRating(BigDecimal rating) {
        if (rating == null) {
            throw new IllegalArgumentException("별점은 필수입니다.");
        }
        if (rating.compareTo(BigDecimal.ONE) < 0 ||
                rating.compareTo(new BigDecimal("5.0")) > 0) {
            throw new IllegalArgumentException("별점은 1.0~5.0 사이여야 합니다.");
        }
    }

    // ===== Helper: 식당 평점·리뷰개수 갱신 =====
    private void updateRestaurantRatingStats(Restaurant restaurant) {
        Long restaurantId = restaurant.getId();
        BigDecimal avgRating = reviewRepository.findAvgRatingByRestaurantId(restaurantId);
        long count = reviewRepository.countByRestaurantIdAndIsHiddenFalse(restaurantId);

        double avg = (avgRating != null)
                ? avgRating.setScale(2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        restaurant.updateRatingStats(avg, (int) count);
    }
}