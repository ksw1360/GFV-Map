package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeCreateRequestDto {
    private String title;
    private String content;
    private String category;    // "서비스" / "점검" / "업데이트" / "기타"
    private Boolean isPinned;
}
