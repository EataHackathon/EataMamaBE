package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    @Query("""
        select distinct m
        from Meal m
        left join fetch m.intakes i
        where m.dayLog.dayLogId = :dayLogId
        order by m.mealTime asc, m.mealId asc
    """)
    List<Meal> findAllByDayLogIdWithIntakes(@Param("dayLogId") Long dayLogId);

    @Query("""
        select distinct m
        from Meal m
        left join fetch m.intakes i
        where m.mealId = :mealId
          and m.dayLog.user.id = :userId
    """)
    Optional<Meal> findByIdWithIntakesAndOwner(Long mealId, Long userId);
}
