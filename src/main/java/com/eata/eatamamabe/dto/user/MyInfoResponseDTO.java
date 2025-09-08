package com.eata.eatamamabe.dto.user;

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
public class MyInfoResponseDTO {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Integer height;
    private Integer weight;
    private Integer week;
    private List<ConditionDto> conditions;
    private List<AllergyDto> allergies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}