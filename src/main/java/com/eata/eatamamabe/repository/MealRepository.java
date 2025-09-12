package com.eata.eatamamabe.repository;

import com.eata.eatamamabe.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
}
