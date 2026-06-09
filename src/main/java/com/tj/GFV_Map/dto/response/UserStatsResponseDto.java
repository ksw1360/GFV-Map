package com.tj.GFV_Map.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatsResponseDto {
    private long totalUsers;        // 총 사용자 수
    private double dailyAverage;    // 일 평균 사용자 수 (총 ÷ 운영 일수)
    private double monthlyAverage;  // 월 평균 사용자 수 (총 ÷ 운영 월수)
    private long operatingDays;     // 운영 일수 (참고용)
    private long operatingMonths;   // 운영 월수 (참고용)
}
