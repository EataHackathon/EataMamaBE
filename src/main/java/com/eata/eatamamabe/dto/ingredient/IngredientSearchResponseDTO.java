package com.eata.eatamamabe.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientSearchResponseDTO {
    private Long ingredientId;
    private String ingredientName;
    private Long ingredientKcal;
    private Long gram;
}
