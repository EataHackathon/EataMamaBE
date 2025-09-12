package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.meal.*;
import com.eata.eatamamabe.entity.DayLog;
import com.eata.eatamamabe.entity.Intake;
import com.eata.eatamamabe.entity.Meal;
import com.eata.eatamamabe.entity.User;
import com.eata.eatamamabe.repository.DayLogRepository;
import com.eata.eatamamabe.repository.IntakeRepository;
import com.eata.eatamamabe.repository.MealRepository;
import com.eata.eatamamabe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final DayLogRepository dayLogRepository;
    private final UserRepository userRepository;
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

    @Transactional
    public void saveMealAdvice(Long mealId, String advice) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("meal not found: " + mealId));
        meal.setMealAdvice(advice);
    }

    @Transactional
    public MealCreateResponseDTO createMeal(Long userId, MealCreateRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        DayLog dayLog = dayLogRepository.findByUserAndLogDate(user, req.getLogDate())
                .orElseGet(() -> {
                    DayLog dl = new DayLog();
                    dl.setLogDate(req.getLogDate());
                    dl.setUser(user);
                    return dayLogRepository.save(dl);
                });

        LocalDateTime mealTime = (req.getMealTime() != null) ? req.getMealTime() : LocalDateTime.now();

        Meal meal = Meal.builder()
                .mealType(req.getMealType())
                .mealName(req.getMealName())
                .mealTime(mealTime)
                .dayLog(dayLog)
                .build();

        for (IntakeCreateRequestDTO i : req.getIntakes()) {
            Intake intake = Intake.builder()
                    .intakeName(i.getIntakeName())
                    .intakeKcal(i.getIntakeKcal())
                    .gram(z(i.getGram()))
                    .carbo(z(i.getCarbo()))
                    .protein(z(i.getProtein()))
                    .fat(z(i.getFat()))
                    .dietaryFiber(z(i.getDietaryFiber()))
                    .meal(meal)
                    .build();
            meal.getIntakes().add(intake);
        }

        Meal saved = mealRepository.save(meal);
        return new MealCreateResponseDTO(dayLog.getDayLogId(), saved.getMealId());
    }

    private Long z(Long v) { return (v == null) ? 0L : v; }
}
