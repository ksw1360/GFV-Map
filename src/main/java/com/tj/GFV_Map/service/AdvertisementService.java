package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.AdCreateRequestDto;
import com.tj.GFV_Map.dto.request.AdUpdateRequestDto;
import com.tj.GFV_Map.dto.response.AdResponseDto;
import com.tj.GFV_Map.entity.Advertisement;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.UserRole;
import com.tj.GFV_Map.repository.AdvertisementRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

    private final AdvertisementRepository adRepository;
    private final UserRepository userRepository;

    // ===== 광고 등록 (ADMIN) =====
    @Transactional
    public AdResponseDto createAd(Long adminId, AdCreateRequestDto req) {
        User admin = verifyAdmin(adminId);

        Advertisement ad = Advertisement.builder()
                .author(admin)
                .title(req.getTitle())
                .imageUrl(req.getImageUrl())
                .linkUrl(req.getLinkUrl())
                .isActive(req.getIsActive())
                .build();

        return AdResponseDto.from(adRepository.save(ad));
    }

    // ===== 광고 수정 (ADMIN) =====
    @Transactional
    public AdResponseDto updateAd(Long adminId, Long adId, AdUpdateRequestDto req) {
        verifyAdmin(adminId);

        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다."));

        ad.update(req.getTitle(), req.getImageUrl(), req.getLinkUrl(), req.getIsActive());
        return AdResponseDto.from(ad);
    }

    // ===== 광고 삭제 (ADMIN, hard delete) =====
    @Transactional
    public void deleteAd(Long adminId, Long adId) {
        verifyAdmin(adminId);

        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다."));

        adRepository.delete(ad);
    }

    // ===== 사용자: 노출 중인 광고 목록 =====
    public List<AdResponseDto> getActiveAds() {
        return adRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(AdResponseDto::from)
                .toList();
    }

    // ===== 광고 상세 =====
    public AdResponseDto getAd(Long adId) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다."));
        return AdResponseDto.from(ad);
    }

    // ===== 관리자: 전체 광고 (숨김 포함) =====
    public Page<AdResponseDto> getAllAdsForAdmin(Long adminId, Pageable pageable) {
        verifyAdmin(adminId);
        return adRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AdResponseDto::from);
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
