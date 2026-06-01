package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    // 이메일로 가장 최근 코드 조회 (재발급 시 활용)
    Optional<VerificationCode> findTopByEmailOrderByCreatedAtDesc(String email);

    // 이메일+코드+미사용 조건으로 조회 (인증 시 사용)
    Optional<VerificationCode> findByEmailAndCodeAndIsUsedFalse(String email, String code);
}