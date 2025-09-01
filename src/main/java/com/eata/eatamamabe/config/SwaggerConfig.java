package com.eata.eatamamabe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        String socialLoginGuide = """
                        ## 📌 소셜 로그인 테스트 방법(아직 미구현)
                        Swagger에서 직접 실행(`Try it out`)은 OAuth2 리다이렉트 흐름 특성상 동작하지 않습니다.
                        대신 **브라우저 주소창**에서 아래 URL로 이동하세요:
                        
                        - 로컬 Kakao 로그인: [http://localhost:8080/oauth2/authorization/kakao](http://localhost:8080/oauth2/authorization/kakao)
                        - 배포 Kakao 로그인: [http://43.203.72.175/oauth2/authorization/kakao](http://43.203.72.175/oauth2/authorization/kakao)
                        
                         ## 📌 예외 코드 처리(아직 미구현)
                        각 api를 열어보면 Responses Code마다 예시가 하나씩 들어있습니다
                        
                        ## 📌 스웨거 테스트시 유의점
                        바로 밑에 보이는 Servers에서 로컬환경이면 로컬주소를 선택해서, 배포환경이면 배포주소를 선택하고 테스트해주세요.
                        """;

        // Bearer(JWT) 스키마
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(org.springframework.http.HttpHeaders.AUTHORIZATION)
                .description("입력창에는 'Bearer' 없이 **AccessToken 문자열만** 넣으세요.");

        return new OpenAPI()
                .info(new Info()
                        .title("Eatamama API")
                        .version("v1.0")
                        .description(socialLoginGuide))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server (HTTP)"),
                        new Server().url("http://43.203.72.175").description("배포 Development Server (HTTP)")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme)
                )
                // 전역으로 Bearer 필요하게(permitAll 엔드포인트는 무시됨)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    /**
     * 공통 스키마 존재 보장 + 전 API 공통 에러 응답 일괄 추가
     * (그룹 문서/빈 초기화 순서 이슈 대응 + 상태코드별 예시 부착)
     */
    @Bean
    public OpenApiCustomizer globalErrorResponses() {
        return openApi -> {
            // --- 스키마 존재 보장 ---
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            var components = openApi.getComponents();
            if (components.getSchemas() == null) {
                components.setSchemas(new LinkedHashMap<>());
            }

            // --- 공통 에러 응답 부착 (상태코드별 예시) ---
            if (openApi.getPaths() == null) return;

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(op -> {
                        op.getResponses().addApiResponse("400",
                                errorResponse("잘못된 요청(Validation/Binding)", "REQ.VALIDATION", "입력값을 확인해주세요."));
                        op.getResponses().addApiResponse("401",
                                errorResponse("인증 필요", "AUTH.UNAUTHORIZED", "로그인이 필요합니다."));
                        op.getResponses().addApiResponse("403",
                                errorResponse("권한 없음", "AUTH.FORBIDDEN", "접근 권한이 없습니다."));
                        op.getResponses().addApiResponse("404",
                                errorResponse("리소스 없음", "DATA.NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."));
                        op.getResponses().addApiResponse("409",
                                errorResponse("중복/제약 위반", "DATA.DUPLICATE", "이미 존재하는 데이터입니다."));
                        op.getResponses().addApiResponse("413",
                                errorResponse("업로드 용량 초과", "UPLOAD.TOO_LARGE", "파일 용량 제한을 초과했습니다."));
                        op.getResponses().addApiResponse("500",
                                errorResponse("백엔드 서버 내부 오류", "SYS.INTERNAL", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
                    })
            );
        };
    }

    // --- util: 상태코드/설명/에러코드/메시지를 받아 예시 포함 ApiResponse 생성 ---
    private ApiResponse errorResponse(String description, String code, String message) {
        boolean is500 = "SYS.INTERNAL".equals(code); // 또는 description/코드로 판별
        Map<String, Object> exampleMap = new LinkedHashMap<>();
        exampleMap.put("status", is500 ? "ERROR" : "FAIL");  // 4xx=FAIL, 5xx=ERROR
        exampleMap.put("serverDateTime", "2025-08-09T12:34:56.789");
        exampleMap.put("errorCode", code);
        exampleMap.put("errorMessage", message);
        exampleMap.put("data", null);

        Example example = new Example()
                .summary("예시")
                .value(exampleMap);

        MediaType mediaType = new MediaType()
                .schema(new Schema<>().$ref("#/components/schemas/Response_Error"))
                .addExamples("default", example);

        Content content = new Content().addMediaType("application/json", mediaType);

        return new ApiResponse().description(description).content(content);
    }
}
