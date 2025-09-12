package com.eata.eatamamabe.dto.meal;

import com.eata.eatamamabe.entity.enums.MealType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MealCreateRequestDTO {
    @Schema(description = "하루 기준 날짜(yyyy-MM-dd)", example = "2025-09-03", required = true)
    private String logDate;

    @Schema(description = "식사 타입", example = "LUNCH", required = true)
    private MealType mealType;

    @Schema(description = "식사명", example = "도시락", required = true)
    private String mealName;

    @Schema(description = "식사 시각(미전달 시 서버 현재시각 사용)", example = "2025-09-03T12:05:00")
    private LocalDateTime mealTime;

    @ArraySchema(
            arraySchema = @Schema(description = "섭취 항목 목록"),
            schema = @Schema(implementation = IntakeCreateRequestDTO.class)
    )
    private List<IntakeCreateRequestDTO> intakes;
}