package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {

    // 특정 날짜 통계 (배치 재실행 시 중복 방지용)
    Optional<UserStatistics> findByStatDate(LocalDate statDate);

    // 전체 통계 (최신 날짜순) - 추이 조회
    List<UserStatistics> findAllByOrderByStatDateDesc();

    // 가장 최근 통계 1건 (대시보드 대표값)
    Optional<UserStatistics> findTopByOrderByStatDateDesc();
}