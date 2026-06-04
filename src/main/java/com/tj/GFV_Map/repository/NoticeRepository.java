package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 사용자용: 노출된 공지만 (고정 먼저, 그다음 최신순)
    Page<Notice> findByIsVisibleTrueOrderByIsPinnedDescCreatedAtDesc(Pageable pageable);

    // 카테고리별 (노출된 것만)
    Page<Notice> findByIsVisibleTrueAndCategoryOrderByIsPinnedDescCreatedAtDesc(
            String category, Pageable pageable);

    // 관리자용: 전체 (숨김 포함)
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
