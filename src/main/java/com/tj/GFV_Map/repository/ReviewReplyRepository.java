package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    // 리뷰 ID로 답글 조회 (1:1 관계라 단건)
    Optional<ReviewReply> findByReviewId(Long reviewId);
}
