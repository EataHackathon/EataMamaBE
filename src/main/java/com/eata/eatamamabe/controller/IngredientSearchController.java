package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.dto.search.IngredientDetailRequest;
import com.eata.eatamamabe.dto.search.IngredientDetailResponse;
import com.eata.eatamamabe.service.IngredientSearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/search")
public class IngredientSearchController {

    private final IngredientSearchService ingredientSearchService;

    @Operation(
            summary = "재료 상세 검색 + AI 추천",
            description = """
                재료명으로 DB에서 영양정보를 조회하고,
                해당 재료로 만들 수 있는 임산부 친화 레시피를 1~3개 추천합니다.
                - risk: GOOD | OK | CAUTION
                - recommendations[n]: { title, summary, whyGood[] }
                """
    )
    @PostMapping("/ingredient/detail")
    public ResponseEntity<IngredientDetailResponse> ingredientDetail(@RequestBody IngredientDetailRequest request) {
        IngredientDetailResponse resp = ingredientSearchService.detail(request);
        return ResponseEntity.ok(resp);
    }
}