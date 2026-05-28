package com.tj.GFV_Map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reviews",
        indexes = {
                @Index(name = "idx_review_restaurant_created",
                        columnList = "review_restaurant_id, review_created_at DESC"),
                @Index(name = "idx_review_user_created",
                        columnList = "review_user_id, review_created_at DESC")
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_restaurant_id", nullable = false)
    private com.tj.GFV_Map.entity.Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_user_id", nullable = false)
    private User user;

    @Column(name = "review_rating", nullable = false)
    private Double rating;

    @Column(name = "review_taste_rating")
    private Double tasteRating;

    @Column(name = "review_service_rating")
    private Double serviceRating;

    @Column(name = "review_atmosphere_rating")
    private Double atmosphereRating;

    @Column(name = "review_price_rating")
    private Double priceRating;

    @Column(name = "review_content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "review_photos", columnDefinition = "json")
    private List<String> photos;

    @Column(name = "review_visit_date")
    private LocalDate visitDate;

    @Column(name = "review_companion_count")
    private Integer companionCount;

    @Column(name = "review_recommended_menu", length = 255)
    private String recommendedMenu;

    @Column(name = "review_like_count")
    private Integer likeCount = 0;

    @Column(name = "review_sentiment_score")
    private Double sentimentScore;

    @CreationTimestamp
    @Column(name = "review_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Review(com.tj.GFV_Map.entity.Restaurant restaurant,
                   User user,
                   Double rating,
                   Double tasteRating,
                   Double serviceRating,
                   Double atmosphereRating,
                   Double priceRating,
                   String content,
                   List<String> photos,
                   LocalDate visitDate,
                   Integer companionCount,
                   String recommendedMenu) {
        this.restaurant = restaurant;
        this.user = user;
        this.rating = rating;
        this.tasteRating = tasteRating;
        this.serviceRating = serviceRating;
        this.atmosphereRating = atmosphereRating;
        this.priceRating = priceRating;
        this.content = content;
        this.photos = photos;
        this.visitDate = visitDate;
        this.companionCount = companionCount;
        this.recommendedMenu = recommendedMenu;
    }

    public void updateContent(String content, Double rating, List<String> photos) {
        this.content = content;
        this.rating = rating;
        this.photos = photos;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void updateSentimentScore(Double score) {
        this.sentimentScore = score;
    }
}