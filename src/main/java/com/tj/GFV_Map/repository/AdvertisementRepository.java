package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    // 사용자: 노출 중인 광고만 (최신순)
    List<Advertisement> findByIsActiveTrueOrderByCreatedAtDesc();

    // 관리자: 전체 광고 (숨김 포함, 최신순)
    Page<Advertisement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
