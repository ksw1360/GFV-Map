package com.tj.GFV_Map.entity;

import com.tj.GFV_Map.enums.PhotoType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "photos",
        indexes = {
                @Index(name = "idx_photo_restaurant", columnList = "photo_restaurant_id, photo_type"),
                @Index(name = "idx_photo_menu", columnList = "photo_menu_id"),
                @Index(name = "idx_photo_review", columnList = "photo_review_id"),
                @Index(name = "idx_photo_uploader", columnList = "photo_uploaded_by")
        }
)
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "photo_type", nullable = false, length = 20)
    private PhotoType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_uploaded_by")
    private User uploadedBy;

    @Column(name = "photo_caption", length = 255)
    private String caption;

    @Column(name = "photo_display_order")
    private Integer displayOrder = 0;

    @Column(name = "photo_is_main")
    private Boolean isMain = false;

    @CreationTimestamp
    @Column(name = "photo_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Photo(String url, PhotoType type, Restaurant restaurant, Menu menu,
                  Review review, User uploadedBy, String caption,
                  Integer displayOrder, Boolean isMain) {
        this.url = url;
        this.type = type;
        this.restaurant = restaurant;
        this.menu = menu;
        this.review = review;
        this.uploadedBy = uploadedBy;
        this.caption = caption;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.isMain = isMain != null ? isMain : false;
    }

    // ✨ 정적 팩토리 메서드: 타입별 안전한 생성 (잘못된 FK 조합 방지)
    public static Photo forRestaurant(String url, Restaurant restaurant, User uploader, String caption) {
        return Photo.builder()
                .url(url)
                .type(PhotoType.RESTAURANT)
                .restaurant(restaurant)
                .uploadedBy(uploader)
                .caption(caption)
                .build();
    }

    public static Photo forMenu(String url, Menu menu, User uploader, String caption) {
        return Photo.builder()
                .url(url)
                .type(PhotoType.MENU)
                .menu(menu)
                .restaurant(menu.getRestaurant())   // 식당 ID도 자동 채움
                .uploadedBy(uploader)
                .caption(caption)
                .build();
    }

    public static Photo forReview(String url, Review review, User uploader, String caption) {
        return Photo.builder()
                .url(url)
                .type(PhotoType.REVIEW)
                .review(review)
                .restaurant(review.getRestaurant()) // 식당 ID도 자동 채움
                .uploadedBy(uploader)
                .caption(caption)
                .build();
    }

    public void markAsMain() { this.isMain = true; }
    public void updateCaption(String caption) { this.caption = caption; }
}