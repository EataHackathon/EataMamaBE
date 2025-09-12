package com.eata.eatamamabe.dto.meal;

import com.eata.eatamamabe.entity.enums.MealType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MealCreateRequestDTO {
    private String logDate;           // "2025-09-03" (그대로 문자열 사용)
    private MealType mealType;
    private String mealName;
    // 비워오면 now()로 대체
    private LocalDateTime mealTime;
    private List<IntakeCreateRequestDTO> intakes;
}