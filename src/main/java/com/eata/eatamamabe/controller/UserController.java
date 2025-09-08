package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.config.security.CustomUserDetails;
import com.eata.eatamamabe.dto.common.Response;
import com.eata.eatamamabe.dto.user.*;
import com.eata.eatamamabe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "내 정보 조회",
            description = "로그인된 사용자의 정보를 조회합니다.(알레르기와 컨디션 정보를 모두 가져옵니다)"
    )
    @GetMapping("/api/user/info")
    public ResponseEntity<Response<MyInfoGetResponseDTO>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(Response.success(userService.getMyInfo(principal.getId())));
    }

    @Operation(
            summary = "내정보 등록(덮어쓰기)",
            description = "로그인 사용자의 키/몸무게/임신 주수?, 질환/알레르기를 등록합니다. 기존 값이 있어도 요청에 포함된 필드는 덮어씌웁니다. " +
                    "목록(conditions, allergies)은 요청에 포함된 경우에 한해 전체 교체됩니다."
    )

    @PostMapping("/api/user/info")
    public ResponseEntity<Response<MyInfoCreateResponseDTO>> createMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody MyInfoCreateRequestDTO request
    ) {
        return ResponseEntity.ok(Response.success(userService.createMyInfo(principal.getId(), request)));
    }

    @Operation(
            summary = "내정보 수정",
            description = """
            - 숫자 필드(height, weight, week)는 값이 있을 때만 갱신합니다.
            - conditions/allergies는 요청 배열을 기준으로 동기화합니다.
            - id가 있으면 해당 항목의 이름을 수정
            - id가 없으면 신규 추가
            - 요청에서 빠진 기존 항목은 삭제(orphanRemoval)
            - conditionId나 allergieId이 없는데 수정하려 하면 오류가 나도록 구현"""
    )

    @PatchMapping("/api/user/info")
    public ResponseEntity<Response<MyInfoUpdateResponseDTO>> patchMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody MyInfoUpdateRequestDTO request
    ) {
        MyInfoUpdateResponseDTO res = userService.patchMyInfo(principal.getId(), request);
        return ResponseEntity.ok(Response.success(res));
    }
}
