package com.eata.eatamamabe.config.security.oauth;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * OAuth2 로그인 시 내려오는 사용자 정보를 표준화해서 담는 DTO
 * - provider: 어떤 소셜인지 (예: "kakao")
 * - providerId: 소셜에서 제공하는 고유 ID
 * - email: 이메일 (없을 수 있음)
 * - nickname: 닉네임
 * - attributes: 원본 응답 전체 Map (필요할 때 직접 접근 가능)
 */
@Getter
public class OAuthAttributes {

    private final String provider;
    private final String providerId;
    private final String email;
    private final String nickname;
    private final Map<String, Object> attributes;

    @Builder
    public OAuthAttributes(String provider, String providerId, String email,
                           String nickname, Map<String, Object> attributes) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.attributes = attributes;
    }

    /**
     * OAuthAttributes 생성 진입점
     * - 추후 Kakao 말고 Google, Naver 확장도 가능
     */
    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    /**
     * Kakao 응답 파싱
     * 카카오 응답 구조:
     * {
     *   id=12345,
     *   kakao_account={email=..., profile={nickname=...}},
     *   properties={nickname=...}
     * }
     */
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String email = account != null ? (String) account.get("email") : null;
        String nickname = null;

        if (properties != null && properties.get("nickname") != null) {
            nickname = (String) properties.get("nickname");
        } else if (account != null && account.get("profile") != null) {
            Map<String, Object> profile = (Map<String, Object>) account.get("profile");
            nickname = (String) profile.get("nickname");
        }

        return OAuthAttributes.builder()
                .provider("kakao")
                .providerId(providerId)
                .email(email)
                .nickname(nickname != null ? nickname : "사용자")
                .attributes(attributes)
                .build();
    }
}