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
import java.util.*;
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
                .orElseThrow(() -> CustomException.notFound("식단을 찾을수 없습니다 : " + mealId));

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
                .orElseThrow(() -> CustomException.notFound("유저를 찾을수 없습니다 : " + userId));

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

    @Transactional(readOnly = true)
    public DayLogDetailResponseDTO getMealsByDate(Long userId, String logDate) {
        // 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을수 없습니다 : " + userId));

        // DayLog 조회(없으면 예외 or 빈 데이터 정책 선택)
        DayLog dayLog = dayLogRepository.findByUserAndLogDate(user, logDate)
                .orElseThrow(() -> CustomException.notFound("현재 날짜에 대한 데이로그가 없습니다 : " + logDate));

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

    @Transactional
    public MealUpdateResponseDTO updateMeal(Long userId, Long mealId, MealUpdateRequestDTO req) {
        // 0) 사용자 존재 검증
        userRepository.findById(userId)
                .orElseThrow(() -> CustomException.notFound("유저를 찾을수 없습니다 : " + userId));

        // 1) 소유자 검증 + intakes 함께 로드
        Meal meal = mealRepository.findByIdWithIntakesAndOwner(mealId, userId)
                .orElseThrow(() -> CustomException.unauthorized("소유자가 아닙니다 : " + userId));

        // 2) 식사 기본 필드 변경
        meal.changeBasics(req.getMealType(), req.getMealName(), req.getMealTime(), req.getMealAdvice());

        // 3) 섭취항목 동기화(서비스 책임)
        // 3-1) 기존 맵 구성
        Map<Long, Intake> existing = new HashMap<>();
        for (Intake i : meal.getIntakes()) {
            if (i.getIntakeId() != null) existing.put(i.getIntakeId(), i);
        }

        // 3-2) 요청에 있는 항목 처리(수정 or 추가)
        Set<Long> keepIds = new HashSet<>();
        if (req.getIntakes() != null) {
            for (IntakeUpsertRequestDTO d : req.getIntakes()) {
                if (d.getIntakeId() != null) {
                    // 수정
                    Intake target = existing.get(d.getIntakeId());
                    if (target == null) throw CustomException.notFound("알수없는 intakeId : " + d.getIntakeId());
                    target.updateNutrition(
                            d.getIntakeName(),
                            d.getIntakeKcal(),
                            z(d.getGram()), z(d.getCarbo()), z(d.getProtein()), z(d.getFat()), z(d.getDietaryFiber())
                    );
                    keepIds.add(d.getIntakeId());
                } else {
                    // 추가
                    Intake created = Intake.builder()
                            .intakeName(d.getIntakeName())
                            .intakeKcal(d.getIntakeKcal())
                            .gram(z(d.getGram()))
                            .carbo(z(d.getCarbo()))
                            .protein(z(d.getProtein()))
                            .fat(z(d.getFat()))
                            .dietaryFiber(z(d.getDietaryFiber()))
                            .build();
                    meal.addIntake(created);
                }
            }
        }

        // 3-3) 요청에 없는 기존 항목 제거
        if (!existing.isEmpty()) {
            for (Long oldId : existing.keySet()) {
                if (!keepIds.contains(oldId)) {
                    meal.removeIntakeById(oldId); // orphanRemoval로 DELETE
                }
            }
        }

        // 4) 더티 체킹 → save 호출 불필요
        return new MealUpdateResponseDTO(meal.getMealId());
    }

    private Long z(Long v) { return (v == null) ? 0L : v; }
}
