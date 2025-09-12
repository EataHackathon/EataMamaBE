package com.eata.eatamamabe.service;

import com.eata.eatamamabe.config.exception.CustomException;
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
import java.util.stream.Collectors;

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
    public MealCreateResponseDTO createMeal(Long userId, MealCreateRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을수 없습니다" + userId));

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

    @Transactional(readOnly = true)
    public DayLogDetailResponseDTO getMealsByDate(Long userId, String logDate) {
        // 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을수 없습니다" + userId));

        // DayLog 조회(없으면 예외 or 빈 데이터 정책 선택)
        DayLog dayLog = dayLogRepository.findByUserAndLogDate(user, logDate)
                .orElseThrow(() -> CustomException.notFound("현재 날짜에 대한 데이로그가 없습니다" + logDate));

        // Meal + Intake fetch join으로 일괄 로드
        var meals = mealRepository.findAllByDayLogIdWithIntakes(dayLog.getDayLogId());

        var mealDtos = meals.stream().map(m -> {
            var intakeDtos = m.getIntakes().stream()
                    .map(i -> new IntakeSimpleDTO(i.getIntakeId(), i.getIntakeName(), i.getIntakeKcal()))
                    .collect(Collectors.toList());

            return new MealSummaryDTO(
                    m.getMealId(),
                    m.getMealType(),
                    m.getMealName(),
                    m.getMealAdvice(),
                    intakeDtos
            );
        }).collect(Collectors.toList());

        return new DayLogDetailResponseDTO(
                dayLog.getDayLogId(),
                dayLog.getLogDate(),
                dayLog.getDailyAdvice(),
                mealDtos
        );
    }
}
