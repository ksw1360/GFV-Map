package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.ReviewReply;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewReplyResponseDto {
    private Long replyId;
    private Long reviewId;
    private Long ownerId;
    private String ownerNickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewReplyResponseDto from(ReviewReply reply) {
        return ReviewReplyResponseDto.builder()
                .replyId(reply.getId())
                .reviewId(reply.getReview().getId())
                .ownerId(reply.getUser().getId())
                .ownerNickname(reply.getUser().getNickname())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}