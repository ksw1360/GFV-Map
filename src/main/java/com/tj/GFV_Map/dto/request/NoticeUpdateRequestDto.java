package com.tj.GFV_Map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeUpdateRequestDto {
    private String title;
    private String content;
    private String category;
    private Boolean isPinned;
}

