package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.ReviewReplyCreateRequestDto;
import com.tj.GFV_Map.dto.request.ReviewReplyUpdateRequestDto;
import com.tj.GFV_Map.dto.response.ReviewReplyResponseDto;
import com.tj.GFV_Map.entity.Review;
import com.tj.GFV_Map.entity.ReviewReply;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.ReviewReplyRepository;
import com.tj.GFV_Map.repository.ReviewRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewReplyService {

    private final ReviewReplyRepository replyRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // ===== 답글 작성 =====
    @Transactional
    public ReviewReplyResponseDto createReply(Long userId, ReviewReplyCreateRequestDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Review review = reviewRepository.findById(req.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 권한 체크 1: OWNER 역할인지
        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalStateException("점주만 답글을 작성할 수 있습니다.");
        }

        // 권한 체크 2: 해당 식당의 owner인지
        if (review.getRestaurant().getOwner() == null
                || !review.getRestaurant().getOwner().getId().equals(userId)) {
            throw new IllegalStateException("본인 식당의 리뷰에만 답글을 작성할 수 있습니다.");
        }

        // 중복 체크 (1:1)
        replyRepository.findByReviewId(req.getReviewId()).ifPresent(r -> {
            throw new IllegalStateException(
                    "이미 답글이 작성되었습니다. 수정하시려면 PUT을 사용해주세요.");
        });

        ReviewReply reply = ReviewReply.builder()
                .review(review)
                .user(user)
                .content(req.getContent())
                .build();
        ReviewReply saved = replyRepository.save(reply);

        return ReviewReplyResponseDto.from(saved);
    }

    // ===== 답글 수정 =====
    @Transactional
    public ReviewReplyResponseDto updateReply(Long userId, Long replyId,
                                              ReviewReplyUpdateRequestDto req) {
        ReviewReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));

        // 본인 답글만 수정 가능
        if (!reply.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 답글만 수정할 수 있습니다.");
        }

        reply.updateContent(req.getContent());
        return ReviewReplyResponseDto.from(reply);
    }

    // ===== 답글 삭제 (hard delete) =====
    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        ReviewReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));

        if (!reply.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 답글만 삭제할 수 있습니다.");
        }

        replyRepository.delete(reply);
    }

    // ===== 리뷰의 답글 조회 =====
    public ReviewReplyResponseDto getReplyByReviewId(Long reviewId) {
        ReviewReply reply = replyRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("답글이 없습니다."));
        return ReviewReplyResponseDto.from(reply);
    }
}