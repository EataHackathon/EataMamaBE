package com.eata.eatamamabe.service;

import com.eata.eatamamabe.entity.enums.SearchType;
import com.eata.eatamamabe.repository.FoodRepository;
import com.eata.eatamamabe.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;

    public Slice<?> search(SearchType type, String name, Long lastId, int size) {
        Pageable p = PageRequest.of(0, size); // 정렬은 JPQL ORDER BY에서 처리
        String q = (name == null || name.isBlank()) ? null : name;

        return (type == SearchType.FOOD)
                ? foodRepository.searchFoodsByRelevance(q, lastId, p)
                : ingredientRepository.searchIngredientsByRelevance(q, lastId, p);
    }
}