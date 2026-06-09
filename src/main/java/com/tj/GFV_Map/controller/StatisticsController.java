package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.response.UserStatsResponseDto;
import com.tj.GFV_Map.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 최신 통계 1건 (대시보드 대표값) - ADMIN
    @GetMapping("/admin/users")
    public ResponseEntity<UserStatsResponseDto> getLatest(
            @AuthenticationPrincipal Long adminId) {
        return ResponseEntity.ok(statisticsService.getLatestStats(adminId));
    }

    // 전체 통계 추이 (날짜별) - ADMIN
    @GetMapping("/admin/users/history")
    public ResponseEntity<List<UserStatsResponseDto>> getHistory(
            @AuthenticationPrincipal Long adminId) {
        return ResponseEntity.ok(statisticsService.getAllStats(adminId));
    }

    // 수동 집계 (오늘 기준 계산 후 저장) - ADMIN
    // 평소엔 월초 배치가 호출, 데모/테스트용 수동 트리거
    @PostMapping("/admin/users/aggregate")
    public ResponseEntity<UserStatsResponseDto> aggregate(
            @AuthenticationPrincipal Long adminId) {
        return ResponseEntity.ok(statisticsService.aggregateToday(adminId));
    }
}