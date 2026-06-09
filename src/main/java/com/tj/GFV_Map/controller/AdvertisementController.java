package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.AdCreateRequestDto;
import com.tj.GFV_Map.dto.request.AdUpdateRequestDto;
import com.tj.GFV_Map.dto.response.AdResponseDto;
import com.tj.GFV_Map.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ad")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService adService;

    // ===== 사용자 조회 =====

    // 노출 중인 광고 목록
    @GetMapping
    public ResponseEntity<List<AdResponseDto>> getActiveAds() {
        return ResponseEntity.ok(adService.getActiveAds());
    }

    // 광고 상세
    @GetMapping("/{adId}")
    public ResponseEntity<AdResponseDto> getAd(@PathVariable Long adId) {
        return ResponseEntity.ok(adService.getAd(adId));
    }

    // ===== 관리자 =====

    // 등록
    @PostMapping("/admin")
    public ResponseEntity<AdResponseDto> create(
            @AuthenticationPrincipal Long adminId,
            @RequestBody AdCreateRequestDto req) {
        return ResponseEntity.ok(adService.createAd(adminId, req));
    }

    // 수정
    @PutMapping("/admin/{adId}")
    public ResponseEntity<AdResponseDto> update(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long adId,
            @RequestBody AdUpdateRequestDto req) {
        return ResponseEntity.ok(adService.updateAd(adminId, adId, req));
    }

    // 삭제
    @DeleteMapping("/admin/{adId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long adId) {
        adService.deleteAd(adminId, adId);
        return ResponseEntity.ok("광고가 삭제되었습니다.");
    }

    // 관리자: 전체 목록 (숨김 포함)
    @GetMapping("/admin/all")
    public ResponseEntity<Page<AdResponseDto>> getAllForAdmin(
            @AuthenticationPrincipal Long adminId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adService.getAllAdsForAdmin(adminId, pageable));
    }
}
