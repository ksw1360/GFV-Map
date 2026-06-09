package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Advertisement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdResponseDto {
    private Long adId;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Boolean isActive;
    private String authorNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdResponseDto from(Advertisement ad) {
        return AdResponseDto.builder()
                .adId(ad.getId())
                .title(ad.getTitle())
                .imageUrl(ad.getImageUrl())
                .linkUrl(ad.getLinkUrl())
                .isActive(ad.getIsActive())
                .authorNickname(ad.getAuthor().getNickname())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }
}
