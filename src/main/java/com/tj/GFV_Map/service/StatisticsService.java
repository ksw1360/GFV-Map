package com.tj.GFV_Map.service;


import com.tj.GFV_Map.dto.response.UserStatsResponseDto;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.entity.UserStatistics;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.UserRepository;
import com.tj.GFV_Map.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final UserStatisticsRepository statsRepository;
    private final UserRepository userRepository;

    // ===== 조회: 최신 통계 1건 (대시보드 대표값) - ADMIN =====
    // 테이블에서 SELECT만 한다. 계산 안 함 (월초 배치가 미리 넣어둠)
    public UserStatsResponseDto getLatestStats(Long adminId) {
        verifyAdmin(adminId);
        UserStatistics latest = statsRepository.findTopByOrderByStatDateDesc()
                .orElseThrow(() -> new IllegalStateException("집계된 통계가 없습니다."));
        return UserStatsResponseDto.from(latest);
    }

    // ===== 조회: 전체 통계 추이 (날짜별) - ADMIN =====
    public List<UserStatsResponseDto> getAllStats(Long adminId) {
        verifyAdmin(adminId);
        return statsRepository.findAllByOrderByStatDateDesc()
                .stream()
                .map(UserStatsResponseDto::from)
                .toList();
    }

    // ===== 집계: 오늘 기준 통계 계산 후 테이블에 저장 =====
    // 월초에 배치(스케줄러)로 호출하는 메서드. 수동 호출도 가능.
    @Transactional
    public UserStatsResponseDto aggregateToday(Long adminId) {
        verifyAdmin(adminId);
        LocalDate today = LocalDate.now();

        long totalUsers = userRepository.count();

        long operatingDays = 1;
        long operatingMonths = 1;
        double dailyAverage = 0;
        double monthlyAverage = 0;

        if (totalUsers > 0) {
            LocalDateTime firstSignup = userRepository.findFirstSignupDate();
            LocalDate startDate = firstSignup.toLocalDate();

            operatingDays = ChronoUnit.DAYS.between(startDate, today) + 1;
            if (operatingDays < 1) operatingDays = 1;

            operatingMonths = ChronoUnit.MONTHS.between(startDate, today) + 1;
            if (operatingMonths < 1) operatingMonths = 1;

            dailyAverage = Math.round((double) totalUsers / operatingDays * 100) / 100.0;
            monthlyAverage = Math.round((double) totalUsers / operatingMonths * 100) / 100.0;
        }

        // 같은 날짜 있으면 UPDATE, 없으면 INSERT (배치 재실행 안전)
        UserStatistics stat = statsRepository.findByStatDate(today)
                .orElse(null);

        if (stat == null) {
            stat = UserStatistics.builder()
                    .statDate(today)
                    .totalUsers(totalUsers)
                    .dailyAverage(dailyAverage)
                    .monthlyAverage(monthlyAverage)
                    .operatingDays(operatingDays)
                    .operatingMonths(operatingMonths)
                    .build();
            statsRepository.save(stat);
        } else {
            stat.update(totalUsers, dailyAverage, monthlyAverage, operatingDays, operatingMonths);
        }

        return UserStatsResponseDto.from(stat);
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