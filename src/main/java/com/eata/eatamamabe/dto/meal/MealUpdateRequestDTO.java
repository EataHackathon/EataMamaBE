package com.eata.eatamamabe.dto.meal;

import com.eata.eatamamabe.entity.enums.MealType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MealUpdateRequestDTO {

    @Schema(description = "식사 타입", example = "LUNCH", required = true)
    private MealType mealType;

    @Schema(description = "식사명", example = "도시락(수정)", required = true)
    private String mealName;

    @Schema(description = "식사 시각", example = "2025-09-03T12:15:00")
    private LocalDateTime mealTime;

    @Schema(description = "식사 조언(옵션)", example = "나트륨 주의. 물 많이 마시기.")
    private String mealAdvice;

    @ArraySchema(
            arraySchema = @Schema(description = "섭취 항목 목록(동기화 정책)"),
            schema = @Schema(implementation = IntakeUpsertRequestDTO.class)
    )
    private List<IntakeUpsertRequestDTO> intakes;
}