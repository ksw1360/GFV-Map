package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.enums.VeganType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class RestaurantSearchSpec {

    // 상호명 LIKE
    public static Specification<Restaurant> nameContains(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;  // null 반환 시 조건 무시
            return cb.like(root.get("name"), "%" + keyword + "%");
        };
    }

    // 주소 LIKE
    public static Specification<Restaurant> addressContains(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            return cb.like(root.get("address"), "%" + keyword + "%");
        };
    }

    // 메뉴명 LIKE (JOIN 필요)
    public static Specification<Restaurant> hasMenuContaining(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            Join<Restaurant, Menu> menus = root.join("menus", JoinType.INNER);
            query.distinct(true);  // JOIN 으로 중복 행 생기는 거 제거
            return cb.like(menus.get("name"), "%" + keyword + "%");
        };
    }

    // 채식 단계로 검색 (메뉴 JOIN 필요)
    public static Specification<Restaurant> hasMenuWithVeganType(VeganType veganType) {
        return (root, query, cb) -> {
            if (veganType == null) return null;
            Join<Restaurant, Menu> menus = root.join("menus", JoinType.INNER);
            query.distinct(true);
            return cb.equal(menus.get("veganType"), veganType);
        };
    }
}