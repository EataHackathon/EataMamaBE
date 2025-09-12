package com.eata.eatamamabe.dto.daylog;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DailyAdviceResponse {
    private Long dayLogId;
    private String logDate;

    private String dailyAdvice;

    private Long totalKcal;
    private Long totalCarbo;
    private Long totalProtein;
    private Long totalFat;
    private Long totalFiber;

    private String flag;
    private String rating;
}