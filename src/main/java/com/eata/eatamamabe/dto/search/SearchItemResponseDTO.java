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
public class SearchItemResponseDTO {
    private Long id;
    private String name;
    private Long kcal;
    private Long gram;
}
