package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intake")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intake extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intakeId;

    @Column(nullable = false, length = 50)
    private String intakeName;

    @Column(nullable = false)
    private Long intakeKcal;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mealId")
    private Meal meal;
}
