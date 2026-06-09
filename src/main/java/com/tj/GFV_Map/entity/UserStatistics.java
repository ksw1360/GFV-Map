package com.tj.GFV_Map.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_statistics",
        uniqueConstraints = {
                // 같은 날짜 중복 방지 (하루 한 줄)
                @UniqueConstraint(name = "uidx_stat_date", columnNames = "stat_date")
        }
)
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long id;

    // 통계 기준 날짜 (이 날 집계한 스냅샷)
    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "stat_total_users", nullable = false)
    private long totalUsers;

    @Column(name = "stat_daily_average", nullable = false)
    private double dailyAverage;

    @Column(name = "stat_monthly_average", nullable = false)
    private double monthlyAverage;

    @Column(name = "stat_operating_days", nullable = false)
    private long operatingDays;

    @Column(name = "stat_operating_months", nullable = false)
    private long operatingMonths;

    @CreationTimestamp
    @Column(name = "stat_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private UserStatistics(LocalDate statDate, long totalUsers, double dailyAverage,
                           double monthlyAverage, long operatingDays, long operatingMonths) {
        this.statDate = statDate;
        this.totalUsers = totalUsers;
        this.dailyAverage = dailyAverage;
        this.monthlyAverage = monthlyAverage;
        this.operatingDays = operatingDays;
        this.operatingMonths = operatingMonths;
    }

    // 이미 있는 날짜면 값만 갱신 (배치 재실행 대비)
    public void update(long totalUsers, double dailyAverage, double monthlyAverage,
                       long operatingDays, long operatingMonths) {
        this.totalUsers = totalUsers;
        this.dailyAverage = dailyAverage;
        this.monthlyAverage = monthlyAverage;
        this.operatingDays = operatingDays;
        this.operatingMonths = operatingMonths;
    }
}