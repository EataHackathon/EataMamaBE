package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.dto.common.Response;
import com.eata.eatamamabe.dto.user.MyInfoResponseDTO;
import com.eata.eatamamabe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/user/infor")
    public ResponseEntity<Response<MyInfoResponseDTO>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(Response.success(userService.getMyInfo(principal.getId())));
    }
}
