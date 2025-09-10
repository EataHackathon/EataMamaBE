package com.eata.eatamamabe.dto.search;

import com.eata.eatamamabe.entity.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchItemDetailResponseDTO {
    private Long id;          // foodId or ingredientId
    private String name;      // foodName or ingredientName
    private Long kcal;
    private Long gram;
    private Long carbo;
    private Long protein;
    private Long fat;
    private Long dietaryFiber;
}
