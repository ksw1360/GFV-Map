package com.tj.GFV_Map.dto.request;

import com.tj.GFV_Map.enums.VeganType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantSearchRequestDto {
    public String name;
    public String address;
    public String menu;
    public VeganType veganType;   // 👈 추가
}
