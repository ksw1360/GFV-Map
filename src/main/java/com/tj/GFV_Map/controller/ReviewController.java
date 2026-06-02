package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.ReviewCreateRequestDto;
import com.tj.GFV_Map.dto.request.ReviewUpdateRequestDto;
import com.tj.GFV_Map.dto.response.ReviewResponseDto;
import com.tj.GFV_Map.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewCreateRequestDto req) {
        return ResponseEntity.ok(reviewService.createReview(userId, req));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateRequestDto req) {
        return ResponseEntity.ok(reviewService.updateReview(userId, reviewId, req));
    }

    // 리뷰 삭제 (soft)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    // 식당의 리뷰 목록
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Page<ReviewResponseDto>> getByRestaurant(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByRestaurant(restaurantId, pageable));
    }

    // 내 리뷰 목록 (마이페이지)
    @GetMapping("/my")
    public ResponseEntity<Page<ReviewResponseDto>> getMy(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId, pageable));
    }
}