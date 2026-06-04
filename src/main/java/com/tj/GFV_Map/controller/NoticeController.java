package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.NoticeCreateRequestDto;
import com.tj.GFV_Map.dto.request.NoticeUpdateRequestDto;
import com.tj.GFV_Map.dto.response.NoticeResponseDto;
import com.tj.GFV_Map.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // ===== 사용자 조회 =====

    // 공지 목록 (노출된 것만, 고정 먼저)
    @GetMapping
    public ResponseEntity<Page<NoticeResponseDto>> getNotices(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getNotices(pageable));
    }

    // 카테고리별
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<NoticeResponseDto>> getByCategory(
            @PathVariable String category,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getNoticesByCategory(category, pageable));
    }

    // 공지 상세
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    // ===== 관리자 =====

    // 작성
    @PostMapping("/admin")
    public ResponseEntity<NoticeResponseDto> create(
            @AuthenticationPrincipal Long adminId,
            @RequestBody NoticeCreateRequestDto req) {
        return ResponseEntity.ok(noticeService.createNotice(adminId, req));
    }

    // 수정
    @PutMapping("/admin/{noticeId}")
    public ResponseEntity<NoticeResponseDto> update(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequestDto req) {
        return ResponseEntity.ok(noticeService.updateNotice(adminId, noticeId, req));
    }

    // 숨김/노출 토글
    @PatchMapping("/admin/{noticeId}/visibility")
    public ResponseEntity<NoticeResponseDto> toggleVisibility(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.toggleVisibility(adminId, noticeId));
    }

    // 삭제
    @DeleteMapping("/admin/{noticeId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long noticeId) {
        noticeService.deleteNotice(adminId, noticeId);
        return ResponseEntity.ok("공지가 삭제되었습니다.");
    }

    // 관리자: 전체 목록 (숨김 포함)
    @GetMapping("/admin/all")
    public ResponseEntity<Page<NoticeResponseDto>> getAllForAdmin(
            @AuthenticationPrincipal Long adminId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getAllNoticesForAdmin(adminId, pageable));
    }
}