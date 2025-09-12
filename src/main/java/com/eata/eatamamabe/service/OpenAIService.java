package com.eata.eatamamabe.service;

import com.eata.eatamamabe.dto.meal.MealRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${openai.model}")
    private String model;

    public String generateMealAdvice(MealRequest request) {
        try {
            String systemMsg = "너는 임산부 영양 코치 AI다. 임신 주차와 식단을 고려해 "
                    + "1) 띄어쓰기 포함 33자 이내 2) 한국어로 3) 간결 요약 및 충고를 해줘"
                    + "반드시 지정된 JSON 스키마에 맞춰서 대답해.";

            // intake 리스트 가져오기
            String payloadJson = om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(Map.of("meal", request.getIntake()));

            String userMsg = "다음 식단 데이터를 요약/조언해줘:\n" + payloadJson;

            // JSON Schema
            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "properties", Map.of("summary", Map.of("type", "string")),
                    "required", List.of("summary"),
                    "additionalProperties", false
            );

            // 요청 body
            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", List.of(
                            Map.of("role", "system", "content", systemMsg),
                            Map.of("role", "user", "content", userMsg)
                    ),
                    "text", Map.of(
                            "format", Map.of(
                                    "name", "meal_advice",
                                    "type", "json_schema",
                                    "schema", schema
                            )
                    ),
                    "temperature", 0.2
            );

            // 호출
            Map resp = restTemplate.postForObject("/responses", body, Map.class);

            // AI API 호출 후 결과에서 summary만 parsing
            List<Map<String, Object>> output = (List<Map<String, Object>>) resp.get("output");
            List<Map<String, Object>> content = (List<Map<String, Object>>) output.get(0).get("content");
            String rawJsonText = String.valueOf(content.get(0).get("text"));

            Map<String, Object> parsed = om.readValue(rawJsonText, Map.class);
            Object summary = parsed.get("summary");
            if (summary == null) throw new IllegalStateException("summary 필드가 없습니다.");
            return String.valueOf(summary);
        } catch (Exception e) {
            throw new RuntimeException("MealAdvice 생성 실패: " + e.getMessage(), e);
        }
    }
}
