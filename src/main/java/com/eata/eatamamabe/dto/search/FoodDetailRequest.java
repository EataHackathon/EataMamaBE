package com.eata.eatamamabe.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class FoodDetailRequest {
    private Data data;

    @Getter @Setter
    @NoArgsConstructor
    public static class Data {
        private String foodName;
    }
}