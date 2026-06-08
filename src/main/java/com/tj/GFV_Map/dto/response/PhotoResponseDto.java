package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tj.GFV_Map.entity.Photo;
import com.tj.GFV_Map.enums.PhotoType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhotoResponseDto {
    private Long photoId;
    private String url;
    private PhotoType type;
    private Long restaurantId;
    private Long menuId;
    private String caption;
    private Boolean isMain;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    public static PhotoResponseDto from(Photo photo) {
        return PhotoResponseDto.builder()
                .photoId(photo.getId())
                .url(photo.getUrl())
                .type(photo.getType())
                // getId()는 LAZY 프록시여도 추가 쿼리 없이 FK 값만 반환 (안전)
                .restaurantId(photo.getRestaurant() != null ? photo.getRestaurant().getId() : null)
                .menuId(photo.getMenu() != null ? photo.getMenu().getId() : null)
                .caption(photo.getCaption())
                .isMain(photo.getIsMain())
                .displayOrder(photo.getDisplayOrder())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}
