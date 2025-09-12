package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.search.IngredientDetailRequest;
import com.eata.eatamamabe.dto.search.IngredientDetailResponse;
import com.eata.eatamamabe.entity.Ingredient;
import com.eata.eatamamabe.repository.IngredientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IngredientSearchService {

    private final IngredientRepository ingredientRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 재료명으로 Ingredient 조회 → OpenAI 호출 → 응답 DTO 구성
     */
    public IngredientDetailResponse detail(IngredientDetailRequest req) {
        String name = req.getData().getIngredientName();

        Ingredient ing = ingredientRepository.findFirstByIngredientNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("ingredient not found: " + name));

        // openAI 호출
        String raw = openAIService.generateIngredientAdvice(
                ing.getIngredientName(),
                ing.getGram(),
                ing.getIngredientKcal(),
                ing.getCarbo(),
                ing.getProtein(),
                ing.getFat(),
                ing.getDietaryFiber(),
                3 // 추천 음식 최대 개수 고정
        );

        // DTO 없이 Map으로 파싱
        Map<String, Object> parsed;
        try {
            parsed = om.readValue(raw, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI JSON parsing failed: " + e.getMessage(), e);
        }

        String risk = String.valueOf(parsed.get("risk")); // GOOD | OK | CAUTION

        List<Map<String, Object>> recs =
                (List<Map<String, Object>>) parsed.getOrDefault("recommendations", List.of());

        List<IngredientDetailResponse.Recommendation> recommendations = recs.stream()
                .map(m -> new IngredientDetailResponse.Recommendation(
                        String.valueOf(m.get("title")),
                        String.valueOf(m.get("summary")),
                        (List<String>) m.getOrDefault("whyGood", List.of())
                ))
                .toList();

        // item/ai 블록 구성
        IngredientDetailResponse.Item item = new IngredientDetailResponse.Item(
                ing.getIngredientId(),
                ing.getIngredientName(),
                ing.getGram(),
                ing.getIngredientKcal().doubleValue(),
                ing.getCarbo().doubleValue(),
                ing.getProtein().doubleValue(),
                ing.getFat().doubleValue(),
                ing.getDietaryFiber().doubleValue()
        );

        IngredientDetailResponse.Ai ai = new IngredientDetailResponse.Ai(risk, recommendations);

        // 최종 응답
        return new IngredientDetailResponse(
                new IngredientDetailResponse.Data("INGREDIENT", item, ai),
                "SUCCESS",
                LocalDateTime.now(),
                null,
                null
        );
    }
}