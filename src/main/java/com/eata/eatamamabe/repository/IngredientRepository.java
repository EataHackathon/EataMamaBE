package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.dto.search.SearchItemResponseDTO;
import com.eata.eatamamabe.entity.Ingredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Query("""
        SELECT new com.eata.eatamamabe.dto.search.SearchItemResponseDTO(
            i.ingredientId, i.ingredientName, i.ingredientKcal, i.gram
        )
        FROM Ingredient i
        WHERE (:name IS NULL OR LOWER(i.ingredientName) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:lastId IS NULL OR i.ingredientId < :lastId)
        ORDER BY
          CASE WHEN :name IS NOT NULL AND LOWER(i.ingredientName) = LOWER(:name) THEN 0 ELSE 1 END,
          CASE WHEN :name IS NOT NULL AND LOWER(i.ingredientName) LIKE CONCAT(LOWER(:name), '%') THEN 0 ELSE 1 END,
          CASE WHEN :name IS NOT NULL THEN LOCATE(LOWER(:name), LOWER(i.ingredientName)) ELSE 999999 END,
          CASE WHEN :name IS NOT NULL THEN ABS(LENGTH(i.ingredientName) - LENGTH(:name)) ELSE LENGTH(i.ingredientName) END,
          i.ingredientId DESC
    """)
    Slice<SearchItemResponseDTO> searchIngredientsByRelevance(
            @Param("name") String name,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
