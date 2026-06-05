package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Menu;
import com.tj.GFV_Map.entity.Restaurant;
import com.tj.GFV_Map.enums.VeganType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSearchSpec {

    // 상호명 LIKE
    public static Specification<Restaurant> nameContains(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
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

    // ───────────────────────────────────────────────────────────────
    // [모드 A · 기본] 키워드 + 채식타입을 "같은 메뉴 한 줄"에 적용 (JOIN 1번)
    //   예) menu=라면 & VEGAN  ->  '라면'이라는 메뉴가 VEGAN인 식당만
    // ───────────────────────────────────────────────────────────────
    public static Specification<Restaurant> menuSameRow(String keyword, VeganType veganType) {
        return (root, query, cb) -> {
            boolean hasKeyword = StringUtils.hasText(keyword);
            boolean hasVegan = veganType != null;
            if (!hasKeyword && !hasVegan) return null;

            Join<Restaurant, Menu> menus = root.join("menus", JoinType.INNER);
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            if (hasKeyword) predicates.add(cb.like(menus.get("name"), "%" + keyword + "%"));
            if (hasVegan)   predicates.add(cb.equal(menus.get("veganType"), veganType));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ───────────────────────────────────────────────────────────────
    // [모드 B] 키워드 메뉴와 채식타입 메뉴가 "각각 따로" 있으면 매칭 (JOIN 2번)
    //   예) menu=라면 & VEGAN  ->  라면도 팔고 + (다른) 비건 메뉴도 있는 식당
    //   일행 중 비건/논비건이 섞였을 때 쓰는 모드. sameMenu=false 로 활성화.
    // ───────────────────────────────────────────────────────────────
    public static Specification<Restaurant> hasMenuContaining(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            Join<Restaurant, Menu> menus = root.join("menus", JoinType.INNER);
            query.distinct(true);
            return cb.like(menus.get("name"), "%" + keyword + "%");
        };
    }

    public static Specification<Restaurant> hasMenuWithVeganType(VeganType veganType) {
        return (root, query, cb) -> {
            if (veganType == null) return null;
            Join<Restaurant, Menu> menus = root.join("menus", JoinType.INNER);
            query.distinct(true);
            return cb.equal(menus.get("veganType"), veganType);
        };
    }
}
