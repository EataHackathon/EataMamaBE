package com.eata.eatamamabe.dto.user;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoCreateRequestDTO {

    @Min(0) @Max(300)
    @Schema(description = "키(cm)", example = "175")
    private Integer height;

    @Min(0) @Max(500)
    @Schema(description = "몸무게(kg)", example = "68")
    private Integer weight;

    @Min(0) @Max(45)
    @Schema(description = "임신 주수(week)", example = "22")
    private Integer week;

    @Valid
    @JsonAlias("condition")
    @ArraySchema(
            schema = @Schema(implementation = ConditionDto.class),
            arraySchema = @Schema(
                    description = "질환 목록(포함 시 전체 교체)",
                    example = """
            [
              { "diseaseName": "Gestational Diabetes" },
              { "diseaseName": "Gestational Diabetes2" }
            ]
            """
            )
    )
    private List<ConditionDto> conditions;

    @Valid
    @JsonAlias("allergy")
    @ArraySchema(
            schema = @Schema(implementation = AllergyDto.class),
            arraySchema = @Schema(
                    description = "알레르기 목록(포함 시 전체 교체)",
                    example = """
            [
              { "allergyName": "어쩌고 알레르기" },
              { "allergyName": "저쩌고 알레르기" }
            ]
            """
            )
    )
    private List<AllergyDto> allergies;
}
