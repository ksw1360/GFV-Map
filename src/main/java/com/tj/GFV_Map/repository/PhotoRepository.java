package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // 특정 식당의 사진 목록 (정렬 순서대로)
    List<Photo> findByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId);

    // 내가 등록한 사진 목록 (최신순)
    List<Photo> findByUploadedByIdOrderByCreatedAtDesc(Long userId);
}