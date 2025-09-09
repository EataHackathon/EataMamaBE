package com.eata.eatamamabe.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodSearchResponseDTO {
    private Long foodId;
    private String foodName;
    private Long foodKcal;
    private Long gram;
}
