package com.eata.eatamamabe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    @Column(nullable = false, length = 50)
    private String foodName;

    @Column(nullable = false)
    private Long foodKcal;

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

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients;
}
