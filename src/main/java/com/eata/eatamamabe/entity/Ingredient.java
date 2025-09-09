package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(nullable = false, length = 50)
    private String ingredientName;

    @Column(nullable = false)
    private Long ingredientKcal;

    @Column(nullable = false)
    private Long gram;

    @Column(nullable = false)
    private Long carbo;

    @Column(nullable = false)
    private Long protein;

    @Column(nullable = false)
    private Long fat;

    @Column(nullable = false)
    private Long dietaryFiber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodId", nullable = false)
    private Food food;
}
