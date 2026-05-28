package com.tj.GFV_Map.entity;

import com.tj.GFV_Map.enums.PriceRange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @Column(name = "pref_user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pref_user_id")
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pref_favorite_categories", columnDefinition = "json")
    private List<String> favoriteCategories;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pref_disliked_categories", columnDefinition = "json")
    private List<String> dislikedCategories;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pref_allergies", columnDefinition = "json")
    private List<String> allergies;

    @Column(name = "pref_spicy_level")
    private Integer spicyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "pref_preferred_price_range", length = 50)
    private PriceRange preferredPriceRange;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pref_dietary_restrictions", columnDefinition = "json")
    private List<String> dietaryRestrictions;

    @Builder
    private UserPreference(User user,
                           List<String> favoriteCategories,
                           List<String> dislikedCategories,
                           List<String> allergies,
                           Integer spicyLevel,
                           PriceRange preferredPriceRange,
                           List<String> dietaryRestrictions) {
        this.user = user;
        this.favoriteCategories = favoriteCategories;
        this.dislikedCategories = dislikedCategories;
        this.allergies = allergies;
        this.spicyLevel = spicyLevel;
        this.preferredPriceRange = preferredPriceRange;
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public void update(List<String> favoriteCategories,
                       List<String> dislikedCategories,
                       List<String> allergies,
                       Integer spicyLevel,
                       PriceRange preferredPriceRange,
                       List<String> dietaryRestrictions) {
        this.favoriteCategories = favoriteCategories;
        this.dislikedCategories = dislikedCategories;
        this.allergies = allergies;
        this.spicyLevel = spicyLevel;
        this.preferredPriceRange = preferredPriceRange;
        this.dietaryRestrictions = dietaryRestrictions;
    }
}