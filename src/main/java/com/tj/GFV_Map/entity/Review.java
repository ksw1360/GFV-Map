package com.tj.GFV_Map.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uidx_review_user_restaurant",
                        columnNames = {"review_user_id", "review_restaurant_id"}
                )
        },
        indexes = {
                @Index(name = "idx_review_restaurant_created",
                        columnList = "review_restaurant_id, review_created_at DESC"),
                @Index(name = "idx_review_user_created",
                        columnList = "review_user_id, review_created_at DESC"),
                @Index(name = "idx_review_hidden", columnList = "review_is_hidden")
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_user_id", nullable = false)
    private User user;

    @Column(name = "review_rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;   // 1.0 ~ 5.0

    @Column(name = "review_content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "review_photos", columnDefinition = "json")
    private List<String> photos = new ArrayList<>();

    @Column(name = "review_visit_date")
    private LocalDate visitDate;

    @Column(name = "review_companion_count")
    private Integer companionCount;

    @Column(name = "review_recommended_menu", length = 255)
    private String recommendedMenu;

    @Column(name = "review_is_hidden", nullable = false)
    private Boolean isHidden = false;

    @CreationTimestamp
    @Column(name = "review_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "review_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Review(Restaurant restaurant, User user, BigDecimal rating, String content,
                   List<String> photos, LocalDate visitDate, Integer companionCount,
                   String recommendedMenu) {
        this.restaurant = restaurant;
        this.user = user;
        this.rating = rating;
        this.content = content;
        this.photos = photos != null ? photos : new ArrayList<>();
        this.visitDate = visitDate;
        this.companionCount = companionCount;
        this.recommendedMenu = recommendedMenu;
        this.isHidden = false;
    }

    public void update(BigDecimal rating, String content, List<String> photos,
                       LocalDate visitDate, Integer companionCount, String recommendedMenu) {
        this.rating = rating;
        this.content = content;
        this.photos = photos != null ? photos : new ArrayList<>();
        this.visitDate = visitDate;
        this.companionCount = companionCount;
        this.recommendedMenu = recommendedMenu;
    }

    public void hide() {
        this.isHidden = true;
    }
}