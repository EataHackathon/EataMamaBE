package com.eata.eatamamabe.entity;

import com.eata.eatamamabe.entity.enums.MealType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="meal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = true, length = 255)
    private String mealAdvice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dayLogId")
    private DayLog dayLog;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Intake> intakes = new ArrayList<>();

    /** 기본 필드 변경(널이면 무시) */
    public void changeBasics(MealType type, String name, LocalDateTime time, String advice) {
        if (type != null) this.mealType = type;
        if (name != null) this.mealName = name;
        if (time != null) this.mealTime = time;
        if (advice != null) this.mealAdvice = advice;
    }

    /** 추가 시 양방향 연결 보장 */
    public void addIntake(Intake intake) {
        if (intake == null) return;
        intake.setMeal(this);
        this.intakes.add(intake);
    }

    /** 존재하면 제거 (orphanRemoval=true 로 delete) */
    public boolean removeIntakeById(Long intakeId) {
        if (intakeId == null) return false;
        return this.intakes.removeIf(i -> intakeId.equals(i.getIntakeId()));
    }

}
