package com.eata.eatamamabe.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealRequest {
    private Long mealId;
    private List<MealItemDTO> intake; // 섭취한 애들
}
