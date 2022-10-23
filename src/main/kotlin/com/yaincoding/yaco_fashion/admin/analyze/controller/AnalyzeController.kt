package com.yaincoding.yaco_fashion.admin.analyze.controller

import com.yaincoding.yaco_fashion.admin.analyze.dto.TokenListDto
import com.yaincoding.yaco_fashion.admin.analyze.service.AnalyzeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analyze")
class AnalyzeController(
    private val service: AnalyzeService
) {

    @GetMapping
    fun analyzeWithIndexAnalyzer(query: String, analyzer: String): ResponseEntity<TokenListDto> {
        return ResponseEntity.ok(service.analyze(query, analyzer))
    }
}