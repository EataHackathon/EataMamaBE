package com.eata.eatamamabe.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDetailResponse {
    private Data data;
    private String status;           // SUCCESS / ERROR
    private LocalDateTime serverDateTime;
    private String errorCode;        // null
    private String errorMessage;     // null

    @Getter
    @AllArgsConstructor
    public static class Data {
        private String type;         // "INGREDIENT"
        private Item item;           // DB에서 가져온 재료 정보
        private Ai ai;               // AI 결과
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private Long ingredientId;
        private String ingredientName;
        private Long gram;
        private Double ingredientKcal;
        private Double carbo;
        private Double protein;
        private Double fat;
        private Double dietaryFiber;
    }

    @Getter
    @AllArgsConstructor
    public static class Ai {
        private String risk;         // GOOD | OK | CAUTION
        private List<Recommendation> recommendations;
    }

    @Getter
    @AllArgsConstructor
    public static class Recommendation {
        private String title;        // 요리명
        private String summary;      // 요약
        private List<String> whyGood; // 임산부에게 좋은 이유들
    }
}