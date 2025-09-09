package com.eata.eatamamabe.controller;

import com.eata.eatamamabe.dto.common.Response;
import com.eata.eatamamabe.entity.enums.SearchType;
import com.eata.eatamamabe.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(
            summary = "음식/재료 검색 (커서 방식, 무한스크롤)",
            description = "음식이나 재료를 검색합니다"
    )
    @GetMapping
    public ResponseEntity<Response<Slice<?>>> search(
            @RequestParam SearchType type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(Response.success(
                searchService.search(type, name, lastId, size)
        ));
    }
}
