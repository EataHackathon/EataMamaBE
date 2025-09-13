package com.eata.eatamamabe.config.security.oauth;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.config.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Value("${app.env}")               // dev/prod 분기용
    private String appEnv;

    @Value("${app.frontend.redirect.local}")
    private String redirectLocal;

    @Value("${app.frontend.redirect.dev}")
    private String redirectDev;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails.getId());

        String target = "local".equalsIgnoreCase(appEnv) ? redirectDev + accessToken : redirectLocal + accessToken;
        response.sendRedirect(target);
    }
}
