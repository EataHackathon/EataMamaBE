package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.dto.meal.MealRequest;
import com.eata.eatamamabe.dto.meal.SummaryResponse;
import com.eata.eatamamabe.service.MealService;
import com.eata.eatamamabe.service.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/meals")
@RequiredArgsConstructor
public class MealAdviceController {

    private final MealService mealService;
    private final OpenAIService openAIService;

    @Operation (
            summary = "식단별 요약/조언 생성",
            description = "- mealId에 해당하는 섭취 음식(intake) 목록을 가져와 gpt api에게 요약/조언을 요청한다. "
                    + "- 응답에는 AI가 생성한 요약/조언이 포함된다."
    )
    @PostMapping("/{mealId}/advice")
    public ResponseEntity<SummaryResponse> createAdvice(@PathVariable Long mealId) {
        MealRequest request = mealService.getMealRequest(mealId);
        String summary = openAIService.generateMealAdvice(request);

        mealService.saveMealAdvice(mealId, summary);

        return ResponseEntity.ok(new SummaryResponse(summary));
    }
}
