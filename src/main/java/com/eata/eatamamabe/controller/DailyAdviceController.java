package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.dto.daylog.DailyAdviceResponse;
import com.eata.eatamamabe.service.DailyAdviceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/day-logs")
@RequiredArgsConstructor
public class DailyAdviceController {

    private final DailyAdviceService dailyAdviceService;

    @Operation(
            summary = "하루 요약/조언 생성",
            description = "dayLogId에 속한 섭취 기록을 합산한 후 식단과 함께 gpt api에게 요약/조언을 요청한다."
                    + "- 생성된 summary는 DayLog.dailyAdvice에 저장된다." +
                    "- 응답에는 total 영양성분, flag, rating이 포함된다. "
    )
    /** 하루 요약/조언 생성 + 저장 + 합산치 포함 반환 */
    @PostMapping("/{dayLogId}/advice")
    public ResponseEntity<DailyAdviceResponse> createDailyAdvice(@PathVariable Long dayLogId) {
        return ResponseEntity.ok(dailyAdviceService.generateAndSaveDailyAdvice(dayLogId));
    }
}