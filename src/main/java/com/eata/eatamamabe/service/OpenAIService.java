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

    // 식사별 AI 요약/조언 생성
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

    // 날짜별 요약/조언 생성
    public String generateDailyAdvice(
            String logDate,
            Long totalKcal, Long totalCarbo, Long totalProtein, Long totalFat, Long totalFiber
    ) {
        try {
            String systemMsg =
                    "너는 임산부 영양 코치 AI다. 하루 섭취 총합을 분석해서 "
                            + "1) summary: 하루 총평 및 조언 (90자 이내 한국어 3~4문장)"
                            + "2) flag: 부족하거나 과다한 영양소 하나를 '영양소명 상태' 형식으로 반환 (예: '칼로리 부족', '당 과다', '지방 과다')."
                            + "만약 특별한 이상이 없으면 '적절함'으로 표시.\n"
                            + "3) rating: flag 값이 '부족' 또는 '과다'를 포함하면 'CAUTION', 그렇지 않고 '적절함'이면 'GOOD'"
                            + "반드시 JSON 스키마에 맞춰서 대답해.";

            String payloadJson = om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(Map.of(
                            "logDate", logDate,
                            "totals", Map.of(
                                    "kcal", totalKcal,
                                    "carbo", totalCarbo,
                                    "protein", totalProtein,
                                    "fat", totalFat,
                                    "fiber", totalFiber
                            )
                    ));

            String userMsg = "다음 하루 섭취 총합을 분석해서 summary, flag, rating을 생성해줘:\n" + payloadJson;

            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "summary", Map.of("type", "string"),
                            "flag", Map.of("type", "string"),
                            "rating", Map.of("type", "string", "enum", List.of("GOOD","CAUTION"))
                    ),
                    "required", List.of("summary","flag","rating"),
                    "additionalProperties", false
            );

            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", List.of(
                            Map.of("role", "system", "content", systemMsg),
                            Map.of("role", "user", "content", userMsg)
                    ),
                    "text", Map.of(
                            "format", Map.of(
                                    "name", "daily_advice",
                                    "type", "json_schema",
                                    "schema", schema
                            )
                    ),
                    "temperature", 0.2
            );

            Map resp = restTemplate.postForObject("/responses", body, Map.class);
            List<Map<String, Object>> output = (List<Map<String, Object>>) resp.get("output");
            List<Map<String, Object>> content = (List<Map<String, Object>>) output.get(0).get("content");
            String rawJsonText = String.valueOf(content.get(0).get("text")); // 필요한 거

            Map<String, Object> parsed = om.readValue(rawJsonText, Map.class);

            return om.writeValueAsString(parsed);
        } catch (Exception e) {
            throw new RuntimeException("DailyAdvice 생성 실패: " + e.getMessage(), e);
        }
    }

    // 재료 상세 검색 -> 음식 추천
    public String generateIngredientAdvice(
            String ingredientName,
            Long gram,
            Number kcal, Number carbo, Number protein, Number fat, Number fiber,
            int lines
    ) {
        try {
            int maxLines = (lines <= 0) ? 3 : lines;

            String systemMsg =
                    "너는 임산부 영양 코치 AI다. 아래 '재료 1개' 정보를 보고 JSON만 반환한다.\n" +
                            "요구사항:\n" +
                            "1) risk: 재료 자체 특성만으로 'GOOD' | 'OK' | 'CAUTION' 중 하나로 평가한다.\n" +
                            "2) recommendations: 최대 " + maxLines + "개. 각 항목은 {title, summary, whyGood[]} 형식으로 작성한다.\n" +
                            "   - title: 해당 재료를 활용한 요리명\n" +
                            "   - summary: 문장 1~2개로 요리의 특징과 장점을 간단히 설명한다. (예: '입덧으로 입맛이 없을 때 가볍게 먹기 좋아요', '만들기는 간단한데 맛과 영양은 최고인 요리')\n" +
                            "   - whyGood: 임산부에게 좋은 이유 2~3개를 bullet point 배열로 작성한다. 각 bullet은 15자~25자 사이의 구체적 장점이어야 한다.\n" +
                            "     (예: '단백질과 비타민 보충 가능', '소화가 잘 되어 위 부담 적음', '10분 내 조리 가능')\n" +
                            "3) 반드시 지정된 JSON 스키마만 출력하고 추가 텍스트는 금지한다.\n" +
                            "4) 영양, 소화 부담 등을 고려해 임산부에게 실제로 유익한 요리를 추천한다.";

            // 사용자 페이로드 (DB에서 가져온 값 전달)
            Map<String, Object> userPayload = Map.of(
                    "ingredient", Map.of(
                            "name", ingredientName,
                            "gram", gram,
                            "kcal", kcal,
                            "carbo", carbo,
                            "protein", protein,
                            "fat", fat,
                            "fiber", fiber
                    )
            );

            String userMsg = "다음 재료 정보를 바탕으로 risk와 추천 요리를 생성해줘:\n"
                    + om.writerWithDefaultPrettyPrinter().writeValueAsString(userPayload);

            // JSON Schema (risk: GOOD/OK/CAUTION, recommendations 배열)
            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "risk", Map.of(
                                    "type", "string",
                                    "enum", List.of("GOOD", "OK", "CAUTION")
                            ),
                            "recommendations", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                    "title", Map.of("type", "string"),
                                                    "summary", Map.of("type", "string"),
                                                    "whyGood", Map.of(
                                                            "type", "array",
                                                            "items", Map.of("type", "string"),
                                                            "minItems", 2,
                                                            "maxItems", 3
                                                    )
                                            ),
                                            "required", List.of("title", "summary", "whyGood"),
                                            "additionalProperties", false
                                    ),
                                    "minItems", 1,
                                    "maxItems", maxLines
                            )
                    ),
                    "required", List.of("risk", "recommendations"),
                    "additionalProperties", false
            );

            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", List.of(
                            Map.of("role", "system", "content", systemMsg),
                            Map.of("role", "user", "content", userMsg)
                    ),
                    "text", Map.of(
                            "format", Map.of(
                                    "name", "ingredient_detail",
                                    "type", "json_schema",
                                    "schema", schema
                            )
                    ),
                    "temperature", 0.2
            );

            Map resp = restTemplate.postForObject("/responses", body, Map.class);
            List<Map<String, Object>> output = (List<Map<String, Object>>) resp.get("output");
            List<Map<String, Object>> content = (List<Map<String, Object>>) output.get(0).get("content");

            // 모델이 반환한 JSON 문자열(raw)을 그대로 리턴 (서비스에서 Map으로 파싱)
            return String.valueOf(content.get(0).get("text"));
        } catch (Exception e) {
            throw new RuntimeException("Ingredient advice 생성 실패: " + e.getMessage(), e);
        }
    }

    // 음식 상세 검색
    public String generateFoodDetailAdvice(
            String foodName,
            Long gram,
            Number kcal, Number carbo, Number protein, Number fat, Number fiber,
            List<String> ingredientNames
    ) {
        try {
            String systemMsg =
                    "너는 임산부 영양 코치 AI다. 아래 '음식 1개' 정보를 보고 JSON만 반환한다.\n" +
                            "요구사항:\n" +
                            "1) risk: 음식 전체 관점에서 'GOOD' | 'OK' | 'CAUTION' 중 하나로 평가.\n" +
                            "2) ingredientsAnalysis: 반드시 제공된 ingredients 배열에서만 이름을 선택하여 4~6개 작성한다.\n" +
                            "   - 각 항목 {name, rating, reason}\n" +
                            "   - rating: GOOD | OK | CAUTION\n" +
                            "   - reason: 임산부 관점에서 요약한다.(25자 이하의 문장, 한국어). 영양소(탄수화물/단백질/지방/나트륨 등) 이름을 name으로 쓰지 말 것.\n" +
                            "3) finalSummary: 한두 문장으로 핵심 요약/주의점(한국어).\n" +
                            "4) 반드시 지정된 JSON 스키마만 출력. 추가 텍스트 금지.";

            Map<String, Object> payload = Map.of(
                    "food", Map.of(
                            "name", foodName,
                            "gram", gram,
                            "kcal", kcal,
                            "carbo", carbo,
                            "protein", protein,
                            "fat", fat,
                            "fiber", fiber
                    ),
                    "ingredients", ingredientNames
            );

            String userMsg = "아래 음식 영양정보와 재료 목록을 참고해 risk/ingredientsAnalysis/finalSummary를 생성:\n"
                    + om.writerWithDefaultPrettyPrinter().writeValueAsString(payload);

            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "risk", Map.of("type", "string", "enum", List.of("GOOD", "OK", "CAUTION")),
                            "ingredientsAnalysis", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                    "name", Map.of("type", "string"),
                                                    "rating", Map.of("type", "string", "enum", List.of("GOOD", "OK", "CAUTION")),
                                                    "reason", Map.of("type", "string")
                                            ),
                                            "required", List.of("name", "rating", "reason"),
                                            "additionalProperties", false
                                    ),
                                    "minItems", 4,
                                    "maxItems", 6
                            ),
                            "finalSummary", Map.of("type", "string")
                    ),
                    "required", List.of("risk", "ingredientsAnalysis", "finalSummary"),
                    "additionalProperties", false
            );

            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", List.of(
                            Map.of("role", "system", "content", systemMsg),
                            Map.of("role", "user", "content", userMsg)
                    ),
                    "text", Map.of("format", Map.of(
                            "name", "food_detail",
                            "type", "json_schema",
                            "schema", schema
                    )),
                    "temperature", 0.2
            );

            Map resp = restTemplate.postForObject("/responses", body, Map.class);
            List<Map<String, Object>> output = (List<Map<String, Object>>) resp.get("output");
            List<Map<String, Object>> content = (List<Map<String, Object>>) output.get(0).get("content");
            return String.valueOf(content.get(0).get("text"));
        } catch (Exception e) {
            throw new RuntimeException("Food detail 생성 실패: " + e.getMessage(), e);
        }
    }
}