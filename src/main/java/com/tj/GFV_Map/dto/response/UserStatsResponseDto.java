package com.tj.GFV_Map.dto.response;

import com.tj.GFV_Map.entity.UserStatistics;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserStatsResponseDto {
    private LocalDate statDate;     // 집계 기준일
    private long totalUsers;        // 총 사용자 수
    private double dailyAverage;    // 일 평균
    private double monthlyAverage;  // 월 평균
    private long operatingDays;     // 운영 일수
    private long operatingMonths;   // 운영 월수

    public static UserStatsResponseDto from(UserStatistics s) {
        return UserStatsResponseDto.builder()
                .statDate(s.getStatDate())
                .totalUsers(s.getTotalUsers())
                .dailyAverage(s.getDailyAverage())
                .monthlyAverage(s.getMonthlyAverage())
                .operatingDays(s.getOperatingDays())
                .operatingMonths(s.getOperatingMonths())
                .build();
    }
}