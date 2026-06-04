package com.tj.GFV_Map.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private String category;
    private Boolean isPinned;
    private Boolean isVisible;
    private Integer viewCount;
    private String authorNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoticeResponseDto from(Notice notice) {
        return NoticeResponseDto.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .category(notice.getCategory())
                .isPinned(notice.getIsPinned())
                .isVisible(notice.getIsVisible())
                .viewCount(notice.getViewCount())
                .authorNickname(notice.getAuthor().getNickname())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
