package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.search.FoodDetailRequest;
import com.eata.eatamamabe.dto.search.FoodDetailResponse;
import com.eata.eatamamabe.entity.Food;
import com.eata.eatamamabe.entity.Ingredient;
import com.eata.eatamamabe.repository.FoodRepository;
import com.eata.eatamamabe.repository.IngredientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoodSearchService {

    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper om = new ObjectMapper();

    public FoodDetailResponse detail(FoodDetailRequest req) {
        String name = req.getData().getFoodName();

        Food food = foodRepository.findFirstByFoodNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("food not found: " + name));

        List<String> ingredientNames = ingredientRepository.findByFood_FoodId(food.getFoodId())
                .stream()
                .map(Ingredient::getIngredientName)
                .toList();

        // OpenAI 호출 (음식 단건 + 성분 분석)
        String raw = openAIService.generateFoodDetailAdvice(
                food.getFoodName(),
                food.getGram(),
                food.getFoodKcal(),
                food.getCarbo(),
                food.getProtein(),
                food.getFat(),
                food.getDietaryFiber(),
                ingredientNames
        );

        Map<String, Object> parsed;
        try {
            parsed = om.readValue(raw, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI JSON parsing failed: " + e.getMessage(), e);
        }

        String risk = String.valueOf(parsed.get("risk"));
        String finalSummary = String.valueOf(parsed.get("finalSummary"));

        List<Map<String, Object>> list =
                (List<Map<String, Object>>) parsed.getOrDefault("ingredientsAnalysis", List.of());

        List<FoodDetailResponse.IngredientAnalysis> ingredientsAnalysis = list.stream()
                .map(m -> new FoodDetailResponse.IngredientAnalysis(
                        String.valueOf(m.get("name")),
                        String.valueOf(m.get("rating")),
                        String.valueOf(m.get("reason"))
                ))
                .toList();

        FoodDetailResponse.Item item = new FoodDetailResponse.Item(
                food.getFoodId(),
                food.getFoodName(),
                food.getGram(),
                food.getFoodKcal().doubleValue(),
                food.getCarbo().doubleValue(),
                food.getProtein().doubleValue(),
                food.getFat().doubleValue(),
                food.getDietaryFiber().doubleValue()
        );

        FoodDetailResponse.Ai ai = new FoodDetailResponse.Ai(risk, ingredientsAnalysis, finalSummary);

        return new FoodDetailResponse("FOOD", item, ai);
    }
}