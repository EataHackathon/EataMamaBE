package com.eata.eatamamabe.dto.daylog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NutrientTotals {
    private Long totalKcal;
    private Long totalCarbo;
    private Long totalProtein;
    private Long totalFat;
    private Long totalFiber;
}