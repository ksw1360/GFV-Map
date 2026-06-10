package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.request.PhotoCreateRequestDto;
import com.tj.GFV_Map.dto.response.PhotoResponseDto;
import com.tj.GFV_Map.service.PhotoService;
import com.tj.GFV_Map.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    // 사진 등록 (점주) — 이미 업로드된 이미지 URL을 등록
    @PostMapping
    public ResponseEntity<PhotoResponseDto> upload(
            @AuthenticationPrincipal Long userId,
            @RequestBody PhotoCreateRequestDto req) {
        return ResponseEntity.ok(photoService.uploadPhoto(userId, req));
    }

    // 특정 식당의 사진 목록 (공개)
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<PhotoResponseDto>> getByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(photoService.getPhotosByRestaurant(restaurantId));
    }

    // 사진 삭제 (점주)
    @DeleteMapping("/{photoId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long photoId) {
        photoService.deletePhoto(userId, photoId);
        return ResponseEntity.ok("사진이 삭제되었습니다.");
    }

    // 필드 추가
    private final S3Service s3Service;

    // 메서드 추가 — 파일 받아서 S3 올리고 URL 반환
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile file) {
        String url = s3Service.upload(file, "photos");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
