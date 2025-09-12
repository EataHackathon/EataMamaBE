package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.dto.common.Response;
import com.eata.eatamamabe.dto.meal.MealCreateRequestDTO;
import com.eata.eatamamabe.dto.meal.MealCreateResponseDTO;
import com.eata.eatamamabe.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor
public class MealController {

    private final MealService MealService;

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
        return ResponseEntity.ok(Response.success(MealService.createMeal(principal.getId(), request)));
    }
}
