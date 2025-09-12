package com.eata.eatamamabe.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealItemDTO {
    private Long intakeId;
    private String intakeName;
    private Long intakeKcal;
    private Long gram;
    private Long carbo;
    private Long protein;
    private Long fat;
    private Long dietaryFiber;
}
