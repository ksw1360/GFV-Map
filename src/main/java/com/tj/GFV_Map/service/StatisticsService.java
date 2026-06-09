package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.UserStatsResponseDto;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final UserRepository userRepository;

    // ===== 사용자 통계 (ADMIN) =====
    public UserStatsResponseDto getUserStats(Long adminId) {
        verifyAdmin(adminId);

        long totalUsers = userRepository.count();

        // 가입자가 없으면 0으로 응답
        if (totalUsers == 0) {
            return UserStatsResponseDto.builder()
                    .totalUsers(0)
                    .dailyAverage(0)
                    .monthlyAverage(0)
                    .operatingDays(0)
                    .operatingMonths(0)
                    .build();
        }

        LocalDateTime firstSignup = userRepository.findFirstSignupDate();
        LocalDate startDate = firstSignup.toLocalDate();
        LocalDate today = LocalDate.now();

        // 운영 일수 (최소 1일) — 첫 가입일도 1일로 카운트
        long operatingDays = ChronoUnit.DAYS.between(startDate, today) + 1;
        if (operatingDays < 1) operatingDays = 1;

        // 운영 월수 (최소 1개월)
        long operatingMonths = ChronoUnit.MONTHS.between(startDate, today) + 1;
        if (operatingMonths < 1) operatingMonths = 1;

        double dailyAverage = (double) totalUsers / operatingDays;
        double monthlyAverage = (double) totalUsers / operatingMonths;

        // 소수점 2자리 반올림
        dailyAverage = Math.round(dailyAverage * 100) / 100.0;
        monthlyAverage = Math.round(monthlyAverage * 100) / 100.0;

        return UserStatsResponseDto.builder()
                .totalUsers(totalUsers)
                .dailyAverage(dailyAverage)
                .monthlyAverage(monthlyAverage)
                .operatingDays(operatingDays)
                .operatingMonths(operatingMonths)
                .build();
    }

    // ===== Helper: 관리자 권한 체크 =====
    private void verifyAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
    }
}
