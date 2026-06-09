package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdUpdateRequestDto {
    private String title;       // 수정할 제목 (null이면 유지)
    private String imageUrl;    // 수정할 이미지 URL (null이면 유지)
    private String linkUrl;     // 수정할 링크 (null이면 유지)
    private Boolean isActive;   // 노출 여부 (null이면 유지)
}
