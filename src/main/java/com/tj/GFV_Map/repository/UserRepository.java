package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 이메일 중복 체크 (회원가입 시 사용)
    boolean existsByEmail(String email);

    // ===== 통계용 추가 =====

    // 가장 먼저 가입한 사용자의 가입일 (일/월 평균 계산 기준)
    @Query("SELECT MIN(u.createdAt) FROM User u")
    LocalDateTime findFirstSignupDate();

    // 특정 기간 내 가입자 수 (필요 시 사용)
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}