package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdCreateRequestDto {
    private String title;       // 광고 제목
    private String imageUrl;    // 이미지 URL (필수)
    private String linkUrl;     // 클릭 시 이동 링크 (선택)
    private Boolean isActive;   // 노출 여부 (선택, 기본 true)
}
