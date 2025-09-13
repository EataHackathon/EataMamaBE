package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.dto.search.FoodDetailRequest;
import com.eata.eatamamabe.dto.search.FoodDetailResponse;
import com.eata.eatamamabe.service.FoodSearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/search")
public class FoodSearchController {

    private final FoodSearchService foodSearchService;

    @Operation(
            summary = "음식 상세 검색",
            description = """
                음식명을 받아 DB의 영양정보를 조회하고,
                임산부 관점의 위험도(risk), 성분/재료별 평가, 최종 요약을 생성합니다.
                - risk: GOOD | OK | CAUTION
                - ingredientsAnalysis[n]: { name, rating, reason }
                - finalSummary: 짧은 요약/주의점
                """
    )
    @PostMapping("/food/detail")
    public ResponseEntity<FoodDetailResponse> foodDetail(@RequestBody FoodDetailRequest request) {
        if (request == null || request.getData() == null
                || request.getData().getFoodName() == null
                || request.getData().getFoodName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(foodSearchService.detail(request));
    }
}