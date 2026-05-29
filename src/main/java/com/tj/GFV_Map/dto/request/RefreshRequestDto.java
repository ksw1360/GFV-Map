package com.tj.GFV_Map.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshRequestDto {
    @NotBlank
    private String refreshToken;
}
