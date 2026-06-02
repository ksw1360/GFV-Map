package com.tj.GFV_Map.controller;


import com.tj.GFV_Map.dto.request.ReviewReplyCreateRequestDto;
import com.tj.GFV_Map.dto.request.ReviewReplyUpdateRequestDto;
import com.tj.GFV_Map.dto.response.ReviewReplyResponseDto;
import com.tj.GFV_Map.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review-reply")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService replyService;

    // 답글 작성
    @PostMapping
    public ResponseEntity<ReviewReplyResponseDto> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewReplyCreateRequestDto req) {
        return ResponseEntity.ok(replyService.createReply(userId, req));
    }

    // 답글 수정
    @PutMapping("/{replyId}")
    public ResponseEntity<ReviewReplyResponseDto> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long replyId,
            @RequestBody ReviewReplyUpdateRequestDto req) {
        return ResponseEntity.ok(replyService.updateReply(userId, replyId, req));
    }

    // 답글 삭제
    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long replyId) {
        replyService.deleteReply(userId, replyId);
        return ResponseEntity.ok("답글이 삭제되었습니다.");
    }

    // 리뷰의 답글 조회 (인증 없이도 조회 가능하게 할 수도 있지만 일단 인증 필요)
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ReviewReplyResponseDto> getByReview(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(replyService.getReplyByReviewId(reviewId));
    }
}