package com.eata.eatamamabe.dto.meal;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class IntakeCreateRequestDTO {
    private String intakeName;
    private Long intakeKcal;
    // 요청에 없으면 0으로 기본 처리
    private Long gram;
    private Long carbo;
    private Long protein;
    private Long fat;
    private Long dietaryFiber;
}