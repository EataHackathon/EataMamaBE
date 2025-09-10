package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.search.SearchItemDetailResponseDTO;
import com.eata.eatamamabe.dto.search.SearchItemResponseDTO;
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

    public Slice<SearchItemResponseDTO> search(SearchType type, String name, Long lastId, int size) {
        Pageable p = PageRequest.of(0, size);
        String q = (name == null || name.isBlank()) ? null : name;

        return (type == SearchType.FOOD)
                ? foodRepository.searchFoodsByRelevance(q, lastId, p)
                : ingredientRepository.searchIngredientsByRelevance(q, lastId, p);
    }

    public Slice<SearchItemDetailResponseDTO> searchMeal(String name, Long lastId, int size) {
        String q = (name == null || name.isBlank()) ? null : name;
        return foodRepository.searchMealFoods(q, lastId, PageRequest.of(0, size));
    }
}