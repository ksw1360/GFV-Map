package com.tj.GFV_Map.entity;

import com.tj.GFV_Map.enums.MenuCategory;
import com.tj.GFV_Map.enums.VeganType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "menus",
        indexes = {
                @Index(name = "idx_menu_restaurant", columnList = "menu_restaurant_id"),
                @Index(name = "idx_menu_name", columnList = "menu_name")
        }
)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "menu_name", nullable = false, length = 255)
    private String name;

    @Column(name = "menu_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "menu_price")
    private Integer price;

    @Column(name = "menu_is_signature")
    private Boolean isSignature = false;

    @Column(name = "menu_is_vegan")
    private Boolean isVegan = false;

    @Column(name = "menu_is_gluten_free")
    private Boolean isGlutenFree = false;

    @Column(name = "menu_image_url", length = 500)
    private String imageUrl;

    @Column(name = "menu_display_order")
    private Integer displayOrder = 0;

    @Column(name = "menu_is_available")
    private Boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "menu_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "menu_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //추가
    @Enumerated(EnumType.STRING)
    @Column(name = "menu_category", length = 20)
    private MenuCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_vegan_type", length = 20)
    private VeganType veganType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "menu_allergens", columnDefinition = "json")
    private List<String> allergens;

    @Column(name = "menu_is_active")
    private Boolean isActive = true;

    @Column(name = "menu_is_seasonal")
    private Boolean isSeasonal = false;

    @Builder
    private Menu(Restaurant restaurant,
                 String name,
                 String description,
                 Integer price,
                 Boolean isSignature,
                 Boolean isVegan,
                 Boolean isGlutenFree,
                 String imageUrl,
                 Integer displayOrder,
                MenuCategory category,
                 VeganType veganType,
                 List<String> allergens,
                 Boolean isSeasonal) {
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isSignature = isSignature != null ? isSignature : false;
        this.isVegan = isVegan != null ? isVegan : false;
        this.isGlutenFree = isGlutenFree != null ? isGlutenFree : false;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public void update(String name, String description, Integer price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void markUnavailable() { this.isAvailable = false; }
    public void markAvailable()   { this.isAvailable = true; }

    public void updateCategoryAndVegan(MenuCategory category, VeganType veganType,
                                       List<String> allergens) {
        this.category = category;
        this.veganType = veganType;
        this.allergens = allergens;
    }

    public void deactivate() { this.isActive = false; }
    public void activate()   { this.isActive = true; }
}