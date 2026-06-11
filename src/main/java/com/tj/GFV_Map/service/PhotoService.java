package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.request.PhotoCreateRequestDto;
import com.tj.GFV_Map.dto.response.PhotoResponseDto;
import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.entity.Photo;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.entity.User;
import com.tj.GFV_Map.enums.PhotoType;
import com.tj.GFV_Map.repository.MenuRepository;
import com.tj.GFV_Map.repository.PhotoRepository;
import com.tj.GFV_Map.repository.RestaurantRepository;
import com.tj.GFV_Map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    // ===== 사진 목록 (공개) =====
    public List<PhotoResponseDto> getPhotosByRestaurant(Long restaurantId) {
        return photoRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId).stream()
                .map(PhotoResponseDto::from)
                .toList();
    }

    // ===== 내가 등록한 사진 목록 =====
    public List<PhotoResponseDto> getMyPhotos(Long userId) {
        return photoRepository.findByUploadedByIdOrderByCreatedAtDesc(userId).stream()
                .map(PhotoResponseDto::from)
                .toList();
    }

    // ===== 사진 등록 (점주, URL 방식) =====
    @Transactional
    public PhotoResponseDto uploadPhoto(Long userId, PhotoCreateRequestDto req) {
        if (req.getUrl() == null || req.getUrl().isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다.");
        }
        if (req.getType() == null) {
            throw new IllegalArgumentException("type은 RESTAURANT 또는 MENU여야 합니다.");
        }

        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Photo photo;

        switch (req.getType()) {
            case RESTAURANT -> {
                if (req.getRestaurantId() == null) {
                    throw new IllegalArgumentException("RESTAURANT 사진은 restaurantId가 필요합니다.");
                }
                Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));
                verifyOwner(restaurant, userId);
                photo = Photo.forRestaurant(req.getUrl(), restaurant, uploader, req.getCaption());
            }
            case MENU -> {
                if (req.getMenuId() == null) {
                    throw new IllegalArgumentException("MENU 사진은 menuId가 필요합니다.");
                }
                Menu menu = menuRepository.findById(req.getMenuId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
                verifyOwner(menu.getRestaurant(), userId);
                photo = Photo.forMenu(req.getUrl(), menu, uploader, req.getCaption());
            }
            default -> throw new IllegalArgumentException("type은 RESTAURANT 또는 MENU만 가능합니다.");
        }

        if (Boolean.TRUE.equals(req.getIsMain())) {
            photo.markAsMain();
        }

        Photo saved = photoRepository.save(photo);
        return PhotoResponseDto.from(saved);
    }

    // ===== 사진 삭제 (점주) =====
    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        if (photo.getType() == PhotoType.REVIEW) {
            throw new IllegalStateException("리뷰 사진은 여기서 삭제할 수 없습니다.");
        }

        verifyOwner(photo.getRestaurant(), userId);

        photoRepository.delete(photo);
    }

    // ===== 소유자 검증 =====
    private void verifyOwner(Restaurant restaurant, Long userId) {
        /* // 임시 조치
        if (restaurant == null
                || restaurant.getOwner() == null
                || !restaurant.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("내 가게의 사진만 관리할 수 있습니다.");
        }
        */
    }
}