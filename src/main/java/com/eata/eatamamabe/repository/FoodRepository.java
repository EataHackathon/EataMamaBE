package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.dto.search.SearchItemDetailResponseDTO;
import com.eata.eatamamabe.dto.search.SearchItemResponseDTO;
import com.eata.eatamamabe.entity.Food;
import com.eata.eatamamabe.entity.enums.SearchType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoodRepository extends JpaRepository<Food, Long> {
    @Query("""
        SELECT new com.eata.eatamamabe.dto.search.SearchItemResponseDTO(
            f.foodId, f.foodName, f.foodKcal, f.gram, :type
        )
        FROM Food f
        WHERE (:name IS NULL OR LOWER(f.foodName) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:lastId IS NULL OR f.foodId < :lastId)
        ORDER BY
          CASE WHEN :name IS NOT NULL AND LOWER(f.foodName) = LOWER(:name) THEN 0 ELSE 1 END,
          CASE WHEN :name IS NOT NULL AND LOWER(f.foodName) LIKE CONCAT(LOWER(:name), '%') THEN 0 ELSE 1 END,
          CASE WHEN :name IS NOT NULL THEN LOCATE(LOWER(:name), LOWER(f.foodName)) ELSE 999999 END,
          CASE WHEN :name IS NOT NULL THEN ABS(LENGTH(f.foodName) - LENGTH(:name)) ELSE LENGTH(f.foodName) END,
          f.foodId DESC
    """)
    Slice<SearchItemResponseDTO> searchFoodsByRelevance(
            @Param("name") String name,
            @Param("lastId") Long lastId,
            @Param("type") SearchType type,
            Pageable pageable
    );

    @Query("""
        SELECT new com.eata.eatamamabe.dto.search.SearchItemDetailResponseDTO(
            f.foodId, f.foodName, f.foodKcal, f.gram, f.carbo, f.protein, f.fat, f.dietaryFiber
        )
        FROM Food f
        WHERE (:name IS NULL OR LOWER(f.foodName) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:lastId IS NULL OR f.foodId < :lastId)
        ORDER BY
          CASE WHEN :name IS NOT NULL AND LOWER(f.foodName) = LOWER(:name) THEN 0 ELSE 1 END,
          CASE WHEN :name IS NOT NULL AND LOWER(f.foodName) LIKE CONCAT(LOWER(:name), '%') THEN 0 ELSE 1 END,
          CASE WHEN :name IS NOT NULL THEN LOCATE(LOWER(:name), LOWER(f.foodName)) ELSE 999999 END,
          CASE WHEN :name IS NOT NULL THEN ABS(LENGTH(f.foodName) - LENGTH(:name)) ELSE LENGTH(f.foodName) END,
          f.foodId DESC
    """)
    Slice<SearchItemDetailResponseDTO> searchMealFoods(
            @Param("name") String name,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
