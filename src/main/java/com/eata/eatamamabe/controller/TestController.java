package com.eata.eatamamabe.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Operation(
            summary = "테스트",
            description = "테스트용 컨트롤러입니다"
    )
    @GetMapping("/api/test")
    public void test(){
    }
}
