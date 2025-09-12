package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.dto.common.Response;
import com.eata.eatamamabe.dto.meal.*;
import com.eata.eatamamabe.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @Operation(summary = "식단 등록하기",
            description = """
                    하루 로그(logDate) 기준으로 식단을 등록합니다.
                    - DayLog가 없으면 자동 생성합니다.
                    - mealTime이 비어있으면 현재 시각으로 저장합니다.
                    - intakes의 누락 영양소 값은 0으로 저장합니다.
                    """)
    @PostMapping
    public ResponseEntity<Response<MealCreateResponseDTO>> createMeal(
            @RequestBody MealCreateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(Response.success(mealService.createMeal(principal.getId(), request)));
    }

    @Operation(summary = "식단 가져오기",
            description = "logDate(yyyy-MM-dd) 기준 하루 식단 전체를 반환합니다.")
    @GetMapping
    public ResponseEntity<Response<DayLogDetailResponseDTO>> getMealsByDate(
            @Parameter(description = "하루 기준 날짜(yyyy-MM-dd)", example = "2025-09-03", required = true)
            @RequestParam String logDate,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        DayLogDetailResponseDTO data = mealService.getMealsByDate(principal.getId(), logDate);
        return ResponseEntity.ok(Response.success(data));
    }

    @Operation(summary = "식단 수정하기",
            description = "mealId 기준으로 식단 및 하위 섭취 항목을 동기화 방식으로 수정합니다.")
    @PutMapping("/{mealId}")
    public ResponseEntity<Response<MealUpdateResponseDTO>> updateMeal(
            @Parameter(description = "수정할 Meal의 ID", example = "1") @PathVariable Long mealId,
            @RequestBody MealUpdateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        MealUpdateResponseDTO res = mealService.updateMeal(principal.getId(), mealId, request);
        return ResponseEntity.ok(Response.success(res));
    }
}
