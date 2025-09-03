package com.eata.eatamamabe.config.security;

import com.eata.eatamamabe.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {
    private final Long id;
    private final String provider;     // "kakao"
    private final String providerId;   // 카카오 고유 ID
    private final String email;
    private final String nickname;
    private final Map<String, Object> attributes;

    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.id = user.getId();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.attributes = attributes;
    }

    public static CustomUserDetails from(User user, Map<String, Object> attributes) {
        return new CustomUserDetails(user, attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 필요 시 DB/Enum 연동로 확장 가능
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // 소셜 로그인만 사용 -> 비밀번호 미사용
        return "";
    }

    @Override
    public String getUsername() {
        // JWT subject와 맞추기: 내부 PK를 username으로 사용
        return String.valueOf(id);
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override public String getName() {
        return String.valueOf(id);
    } // OAuth2User용 식별자
}