package com.eata.eatamamabe.service;

import com.eata.eatamamabe.config.exception.CustomException;
import com.eata.eatamamabe.dto.search.FoodDetailRequest;
import com.eata.eatamamabe.dto.search.FoodDetailResponse;
import com.eata.eatamamabe.entity.Food;
import com.eata.eatamamabe.entity.Ingredient;
import com.eata.eatamamabe.repository.FoodRepository;
import com.eata.eatamamabe.repository.IngredientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.SQLExceptionOverride;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoodSearchService {

    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper om;

    public FoodDetailResponse detail(FoodDetailRequest req) {
        // 요청 오류
        if (req == null || req.getData() == null || req.getData().getFoodName() == null
                || req.getData().getFoodName().isBlank()) {
            throw CustomException.badRequest("foodName은 필수 값입니다.");
        }

        String name = req.getData().getFoodName();

        Food food = foodRepository.findFirstByFoodNameIgnoreCase(name)
                .orElseThrow(() -> CustomException.notFound("food not found: " + name));

        List<String> ingredientNames = ingredientRepository.findByFood_FoodId(food.getFoodId())
                .stream()
                .map(Ingredient::getIngredientName)
                .toList();

        // OpenAI 호출 (음식 단건 + 성분 분석)
        String raw;
        try {
            raw = openAIService.generateFoodDetailAdvice(
                    food.getFoodName(),
                    food.getGram(),
                    food.getFoodKcal(),
                    food.getCarbo(),
                    food.getProtein(),
                    food.getFat(),
                    food.getDietaryFiber(),
                    ingredientNames
            );
        } catch (Exception e) {
            throw new CustomException("AI.EXTERNAL_API", "AI 서비스 호출 실패: " + e.getMessage(),
                    HttpStatus.BAD_GATEWAY);
        }

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