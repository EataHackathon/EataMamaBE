package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.dto.daylog.NutrientTotals;
import com.eata.eatamamabe.entity.Intake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IntakeRepository extends JpaRepository<Intake, Long> {
    List<Intake> findByMeal_MealId(Long MealId);

    @Query("""
           select new com.eata.eatamamabe.dto.daylog.NutrientTotals(
               coalesce(sum(i.intakeKcal),0),
               coalesce(sum(i.carbo),0),
               coalesce(sum(i.protein),0),
               coalesce(sum(i.fat),0),
               coalesce(sum(i.dietaryFiber),0)
           )
           from Intake i
           where i.meal.dayLog.dayLogId = :dayLogId
           """)
    NutrientTotals sumTotalsByDayLogId(@Param("dayLogId") Long dayLogId);
}
