package com.eata.eatamamabe.config.security.oauth;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final String provider;
    private final String providerId;
    private final String profileImageUrl;
    private final String email;
    private final String nickname;
    private final Map<String, Object> attributes;

    @Builder
    public OAuthAttributes(String provider, String providerId, String email,
                           String nickname, String profileImageUrl,
                           Map<String, Object> attributes) {
        this.provider = provider;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.nickname = nickname;
        this.attributes = attributes;
    }

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    // ---------- Kakao ----------
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        final String providerId = String.valueOf(attributes.get("id"));

        final Map<String, Object> account    = getMap(attributes, "kakao_account");
        final Map<String, Object> properties = getMap(attributes, "properties");
        final Map<String, Object> profile    = getMap(account,    "profile");

        final String email = getString(account, "email");
        final String nickname = firstNonBlank(
                getString(properties, "nickname"),
                getString(profile,    "nickname"),
                "사용자"
        );
        final String profileImageUrl = firstNonBlank(
                // 신규(v2) 우선
                getString(profile,    "profile_image_url"),
                getString(profile,    "thumbnail_image_url"),
                // 레거시 보조
                getString(properties, "profile_image"),
                getString(properties, "thumbnail_image")
        );

        return OAuthAttributes.builder()
                .provider("kakao")
                .providerId(providerId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .attributes(attributes)
                .build();
    }

    // ---------- tiny helpers ----------
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> parent, String key) {
        if (parent == null) return null;
        Object v = parent.get(key);
        return (v instanceof Map<?, ?> mm) ? (Map<String, Object>) mm : null;
    }

    private static String getString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        return (v instanceof String s && !s.isBlank()) ? s : null;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }
}