package com.eata.eatamamabe.config.security.service;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.entity.User;
import com.eata.eatamamabe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Security 기본 스펙: username으로 유저를 조회.
     * 우리는 JWT subject=내부 PK를 username으로 사용한다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id: " + username);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return CustomUserDetails.from(user, null);
    }

    /**
     * (선택) 카카오 OAuth 성공 시 사용할 헬퍼:
     * provider + providerId 로 유저 조회/가입 후 SecurityContext에 올릴 때 유용.
     */
    public UserDetails loadByProviderAndProviderId(String provider, String providerId) {
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found by provider/providerId: " + provider + "/" + providerId));
        return CustomUserDetails.from(user, null);
    }
}