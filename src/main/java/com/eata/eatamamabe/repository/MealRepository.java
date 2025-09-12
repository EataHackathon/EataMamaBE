package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    @Query("""
        select distinct m
        from Meal m
        left join fetch m.intakes i
        where m.dayLog.dayLogId = :dayLogId
        order by m.mealTime asc, m.mealId asc
    """)
    List<Meal> findAllByDayLogIdWithIntakes(@Param("dayLogId") Long dayLogId);
}
