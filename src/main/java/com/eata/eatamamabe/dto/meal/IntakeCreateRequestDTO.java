package com.eata.eatamamabe.dto.meal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "IntakeCreateRequestDTO", description = "섭취 항목 생성 요청")
public class IntakeCreateRequestDTO {
    @Schema(description = "섭취 항목 이름", example = "김밥", required = true)
    private String intakeName;

    @Schema(description = "해당 항목 kcal", example = "243", required = true)
    private Long intakeKcal;

    @Schema(description = "그램(미전달 시 0 처리)", example = "0")
    private Long gram;

    @Schema(description = "탄수화물 g(미전달 시 0 처리)", example = "0")
    private Long carbo;

    @Schema(description = "단백질 g(미전달 시 0 처리)", example = "0")
    private Long protein;

    @Schema(description = "지방 g(미전달 시 0 처리)", example = "0")
    private Long fat;

    @Schema(description = "식이섬유 g(미전달 시 0 처리)", example = "0")
    private Long dietaryFiber;
}