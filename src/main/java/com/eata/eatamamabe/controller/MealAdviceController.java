package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.dto.meal.MealRequest;
import com.eata.eatamamabe.dto.meal.SummaryResponse;
import com.eata.eatamamabe.service.MealService;
import com.eata.eatamamabe.service.OpenAIService;
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

    // POST /api/ai/meals/{mealId}/advice
    // summary 반환
    @PostMapping("/{mealId}/advice")
    public ResponseEntity<SummaryResponse> createAdvice(@PathVariable Long mealId) {
        MealRequest request = mealService.getMealRequest(mealId);
        String summary = openAIService.generateMealAdvice(request);

        return ResponseEntity.ok(new SummaryResponse(summary));
    }
}
