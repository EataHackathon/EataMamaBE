package com.eata.eatamamabe.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FoodDetailResponse {
    private String type;       // "FOOD"
    private Item item;
    private Ai ai;

    @Getter
    @AllArgsConstructor
    public static class Item {
        private Long foodId;
        private String foodName;
        private Long gram;
        private Double foodKcal;
        private Double carbo;
        private Double protein;
        private Double fat;
        private Double dietaryFiber;
    }

    @Getter
    @AllArgsConstructor
    public static class Ai {
        private String risk;   // GOOD | OK | CAUTION
        private List<IngredientAnalysis> ingredientsAnalysis;
        private String finalSummary;
    }

    @Getter
    @AllArgsConstructor
    public static class IngredientAnalysis { // 재료 분석
        private String name;     // 재료명
        private String rating;   // GOOD | OK | CAUTION
        private String reason;   // 짧은 이유
    }
}