package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.response.UserStatsResponseDto;
import com.tj.GFV_Map.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 사용자 통계 (총/일평균/월평균) - ADMIN
    @GetMapping("/admin/users")
    public ResponseEntity<UserStatsResponseDto> getUserStats(
            @AuthenticationPrincipal Long adminId) {
        return ResponseEntity.ok(statisticsService.getUserStats(adminId));
    }
}
