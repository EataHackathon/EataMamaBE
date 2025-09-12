package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.meal.MealItemDTO;
import com.eata.eatamamabe.dto.meal.MealRequest;
import com.eata.eatamamabe.entity.Intake;
import com.eata.eatamamabe.entity.Meal;
import com.eata.eatamamabe.repository.IntakeRepository;
import com.eata.eatamamabe.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final IntakeRepository intakeRepository;

    public MealRequest getMealRequest(Long mealId){
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("meal not found: " + mealId));

        List<Intake> intakes = intakeRepository.findByMeal_MealId(meal.getMealId());

        List<MealItemDTO> items = intakes.stream()
                .map(i -> new MealItemDTO(
                        i.getIntakeId(),
                        i.getIntakeName(),
                        i.getIntakeKcal(),
                        i.getGram(),
                        i.getCarbo(),
                        i.getProtein(),
                        i.getFat(),
                        i.getDietaryFiber()
                ))
                .toList();
        return new MealRequest(meal.getMealId(), items);
    }
}
