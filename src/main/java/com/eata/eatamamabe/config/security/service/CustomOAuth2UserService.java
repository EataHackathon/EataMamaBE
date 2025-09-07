package com.eata.eatamamabe.config.security.service;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.config.security.oauth.OAuthAttributes;
import com.eata.eatamamabe.entity.User;
import com.eata.eatamamabe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // provider별 표준화된 DTO로 변환
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(provider, attributes);

        // DB 저장 or 조회
        User user = userRepository.findByProviderAndProviderId(
                        oAuthAttributes.getProvider(),
                        oAuthAttributes.getProviderId()
                )
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(oAuthAttributes.getProvider())
                        .providerId(oAuthAttributes.getProviderId())
                        .email(oAuthAttributes.getEmail())
                        .nickname(oAuthAttributes.getNickname())
                        .profileImageUrl(oAuthAttributes.getProfileImageUrl())
                        .build()));

        // SecurityContext에 CustomUserDetails 세팅
        return CustomUserDetails.from(user, attributes);
    }
}
