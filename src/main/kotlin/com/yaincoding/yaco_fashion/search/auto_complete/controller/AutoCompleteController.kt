package com.yaincoding.yaco_fashion.search.auto_complete.controller

import com.yaincoding.yaco_fashion.search.auto_complete.dto.AutoCompleteRequestDto
import com.yaincoding.yaco_fashion.search.auto_complete.dto.AutoCompleteResponseDto
import com.yaincoding.yaco_fashion.search.auto_complete.service.AutoCompleteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auto_complete")
class AutoCompleteController(
    private val service: AutoCompleteService
) {

    @GetMapping
    fun autoComplete(requestDto: AutoCompleteRequestDto): ResponseEntity<AutoCompleteResponseDto> {
        val response: AutoCompleteResponseDto = service.search(requestDto)
        return ResponseEntity.ok(response)
    }
}