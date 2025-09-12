package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.Intake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntakeRepository extends JpaRepository<Intake, Long> {
    List<Intake> findByMeal_MealId(Long MealId);
}
