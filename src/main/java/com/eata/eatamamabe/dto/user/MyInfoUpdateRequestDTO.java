package com.eata.eatamamabe.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(
        name = "MyInfoUpdateRequestDTO",
        description = "내정보 수정 요청(부분 수정 + 목록 동기화). " +
                "숫자 필드는 값이 있을 때만 갱신. " +
                "conditions/allergies는 요청 배열 기준으로 동기화(기존에 없고 id 없는 건 추가, id 있는 건 이름 수정, 요청에서 빠진 건 삭제).",
        example = """
    {
      "height": 160,
      "weight": null,
      "week": 2,
      "conditions": [
        { "conditionId": 1, "diseaseName": "과민성 대장 증후군(수정)" },
        { "diseaseName": "새 질환" }
      ],
      "allergies": [
        { "allergyId": 1, "allergyName": "꽃가루 알레르기(수정)" }
      ]
    }
    """
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoUpdateRequestDTO {

    @Min(0) @Max(300)
    @Schema(description = "키(cm)", nullable = true)
    private Integer height;

    @Min(0) @Max(500)
    @Schema(description = "몸무게(kg)", nullable = true)
    private Integer weight;

    @Min(0) @Max(45)
    @Schema(description = "임신 주수(week)", nullable = true)
    private Integer week;

    @Valid
    @JsonAlias("condition")
    private List<ConditionDto> conditions;

    @Valid
    @JsonAlias("allergy")
    private List<AllergyDto> allergies;
}
