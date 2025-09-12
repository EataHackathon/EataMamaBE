package com.eata.eatamamabe.dto.meal;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class IntakeSimpleDTO {
    private Long intakeId;
    private String intakeName;
    private Long intakeKcal;
}