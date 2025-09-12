package com.eata.eatamamabe.dto.meal;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MealCreateResponseDTO {
    private Long dayLogId;
    private Long mealId;
}