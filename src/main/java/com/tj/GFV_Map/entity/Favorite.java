package com.tj.GFV_Map.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(Favorite.FavoriteId.class)
@Table(
        name = "favorites",
        indexes = {
                @Index(name = "idx_fav_user_created",
                        columnList = "fav_user_id, fav_created_at DESC"),
                @Index(name = "idx_fav_restaurant",
                        columnList = "fav_restaurant_id")
        }
)
public class Favorite {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fav_user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fav_restaurant_id", nullable = false)
    private Restaurant restaurant;

    @CreationTimestamp
    @Column(name = "fav_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Favorite(User user, Restaurant restaurant) {
        this.user = user;
        this.restaurant = restaurant;
    }

    // 복합키 정의 (Java 17 record로 간단하게)
    public record FavoriteId(Long user, Long restaurant) implements Serializable {
        public FavoriteId() {
            this(null, null);
        }
    }
}