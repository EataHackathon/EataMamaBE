package com.eata.eatamamabe.dto.meal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "IntakeUpsertRequestDTO", description = "섭취 항목 수정/추가 요청")
public class IntakeUpsertRequestDTO {

    @Schema(description = "기존 항목이면 필수", example = "1")
    private Long intakeId;

    @Schema(description = "섭취 항목 이름", example = "김밥", required = true)
    private String intakeName;

    @Schema(description = "kcal", example = "250", required = true)
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
