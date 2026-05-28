package com.tj.GFV_Map.repository;

import com.tj.GFV_Map.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /*
    1. 상호명
    2. 지역(주소)
        "서울" and "논현동"
        "대구" and "논현동"
        "%강남역%"
        내 주위 반경 1km
    3. 메뉴
     */
    // 상호명
    List<Restaurant> findByNameContaining(String keyword);

    // 지역(주소)
    List<Restaurant> findByAddressContaining(String keyword);

    // 내 주위 1km 반경
    @Query(value = """
            SELECT * FROM restaurants r
            WHERE ST_Distance_Sphere(
                      r.restaurant_location,
                      ST_GeomFromText(:point, 4326)
                  ) <= :radius
            ORDER BY ST_Distance_Sphere(
                      r.restaurant_location,
                      ST_GeomFromText(:point, 4326)
                  ) ASC
            """, nativeQuery = true)
    List<Restaurant> findNearby(@Param("point") String point,
                                @Param("radius") double radius);
}