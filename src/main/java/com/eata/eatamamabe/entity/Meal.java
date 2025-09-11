package com.eata.eatamamabe.entity;

import com.eata.eatamamabe.entity.enums.MealType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="meal")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Meal extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealId;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(nullable = false, length = 50)
    private String mealName;

    @Column(nullable = false)
    private LocalDateTime mealTime;

    @Column(length = 255)
    private String mealAdvice;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "day_log_id")
//    private DayLog dayLog;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Intake> intakes = new ArrayList<>();

}
