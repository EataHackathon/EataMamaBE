package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.daylog.DailyAdviceResponse;
import com.eata.eatamamabe.dto.daylog.NutrientTotals;
import com.eata.eatamamabe.entity.DayLog;
import com.eata.eatamamabe.repository.DayLogRepository;
import com.eata.eatamamabe.repository.IntakeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DailyAdviceService {

    private final DayLogRepository dayLogRepository;
    private final IntakeRepository intakeRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper om;

    /**
     * dayLogId 기반으로 하루 섭취 성분 합산 → OpenAI 요약 생성 → DAYLOG.dailyAdvice 저장 → 합산치+조언 반환
     */
    @Transactional
    public DailyAdviceResponse generateAndSaveDailyAdvice(Long dayLogId) {
        // dayLog 조회
        DayLog dayLog = dayLogRepository.findById(dayLogId)
                .orElseThrow(() -> new IllegalArgumentException("dayLog not found: " + dayLogId));

        // 영양성분 총합 계산
        NutrientTotals totals = intakeRepository.sumTotalsByDayLogId(dayLogId);

        // openAI 호출
        String raw = openAIService.generateDailyAdvice(
                dayLog.getLogDate(),
                totals.getTotalKcal(),
                totals.getTotalCarbo(),
                totals.getTotalProtein(),
                totals.getTotalFat(),
                totals.getTotalFiber()
        );

        // gpt 응답 중 summary, flag, rating 파싱
        Map<String,Object> parsed;
        try {
            parsed = om.readValue(raw, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패: " + e.getMessage(), e);
        }

        String summary = (String) parsed.get("summary");
        String flag = (String) parsed.get("flag");
        String rating = (String) parsed.get("rating");

        // DB에는 advice만 저장
        dayLog.setDailyAdvice(summary);
        dayLogRepository.save(dayLog);

        return new DailyAdviceResponse(
                dayLog.getDayLogId(),
                dayLog.getLogDate(),
                summary,
                totals.getTotalKcal(),
                totals.getTotalCarbo(),
                totals.getTotalProtein(),
                totals.getTotalFat(),
                totals.getTotalFiber(),
                flag,
                rating
        );
    }
}