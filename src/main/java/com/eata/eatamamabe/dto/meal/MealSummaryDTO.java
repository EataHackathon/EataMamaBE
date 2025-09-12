package com.eata.eatamamabe.dto.meal;

import com.eata.eatamamabe.entity.enums.MealType;
import lombok.*;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MealSummaryDTO {
    private Long mealId;
    private MealType mealType;
    private String mealName;
    private String mealAdvice;
    private List<IntakeSimpleDTO> intakes;
}