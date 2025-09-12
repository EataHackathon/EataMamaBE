package com.eata.eatamamabe.dto.meal;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DayLogDetailResponseDTO {
    private Long dayLogId;
    private String logDate;
    private String dailyAdvice; // null 가능
    private List<MealSummaryDTO> meals;
}
