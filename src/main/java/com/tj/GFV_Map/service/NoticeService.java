package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.NoticeCreateRequestDto;
import com.tj.GFV_Map.dto.request.NoticeUpdateRequestDto;
import com.tj.GFV_Map.dto.response.NoticeResponseDto;
import com.tj.GFV_Map.entity.Notice;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.NoticeRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    // ===== 공지 작성 (ADMIN) =====
    @Transactional
    public NoticeResponseDto createNotice(Long adminId, NoticeCreateRequestDto req) {
        User admin = verifyAdmin(adminId);

        Notice notice = Notice.builder()
                .author(admin)
                .title(req.getTitle())
                .content(req.getContent())
                .category(req.getCategory())
                .isPinned(req.getIsPinned())
                .build();

        return NoticeResponseDto.from(noticeRepository.save(notice));
    }

    // ===== 공지 수정 (ADMIN) =====
    @Transactional
    public NoticeResponseDto updateNotice(Long adminId, Long noticeId, NoticeUpdateRequestDto req) {
        verifyAdmin(adminId);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        notice.update(req.getTitle(), req.getContent(), req.getCategory(), req.getIsPinned());
        return NoticeResponseDto.from(notice);
    }

    // ===== 공지 숨김/노출 토글 (ADMIN) =====
    @Transactional
    public NoticeResponseDto toggleVisibility(Long adminId, Long noticeId) {
        verifyAdmin(adminId);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        if (notice.getIsVisible()) {
            notice.hide();
        } else {
            notice.show();
        }
        return NoticeResponseDto.from(notice);
    }

    // ===== 공지 삭제 (ADMIN, hard delete) =====
    @Transactional
    public void deleteNotice(Long adminId, Long noticeId) {
        verifyAdmin(adminId);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        noticeRepository.delete(notice);
    }

    // ===== 사용자: 공지 목록 (노출된 것만, 고정 먼저) =====
    public Page<NoticeResponseDto> getNotices(Pageable pageable) {
        return noticeRepository
                .findByIsVisibleTrueOrderByIsPinnedDescCreatedAtDesc(pageable)
                .map(NoticeResponseDto::from);
    }

    // ===== 사용자: 카테고리별 공지 =====
    public Page<NoticeResponseDto> getNoticesByCategory(String category, Pageable pageable) {
        return noticeRepository
                .findByIsVisibleTrueAndCategoryOrderByIsPinnedDescCreatedAtDesc(category, pageable)
                .map(NoticeResponseDto::from);
    }

    // ===== 공지 상세 =====
    public NoticeResponseDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        // 숨김 공지는 일반 사용자에게 안 보임
        if (!notice.getIsVisible()) {
            throw new IllegalArgumentException("공지를 찾을 수 없습니다.");
        }

        // 조회수는 지금 OFF — 나중에 켤 때 notice.increaseViewCount() 추가
        return NoticeResponseDto.from(notice);
    }

    // ===== 관리자: 전체 공지 (숨김 포함) =====
    public Page<NoticeResponseDto> getAllNoticesForAdmin(Long adminId, Pageable pageable) {
        verifyAdmin(adminId);
        return noticeRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(NoticeResponseDto::from);
    }

    // ===== Helper: 관리자 권한 체크 =====
    private User verifyAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
        return admin;
    }
}