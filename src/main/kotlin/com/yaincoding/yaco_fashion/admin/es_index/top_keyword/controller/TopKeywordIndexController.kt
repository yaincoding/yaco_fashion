package com.yaincoding.yaco_fashion.admin.es_index.top_keyword.controller

import com.yaincoding.yaco_fashion.admin.es_index.top_keyword.service.TopKeywordIndexService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/index/top_keyword")
class TopKeywordIndexController(
    private val service: TopKeywordIndexService
) {

    @GetMapping("/reindex")
    fun reindex(): ResponseEntity<HttpStatus> {
        service.reindex()
        return ResponseEntity(HttpStatus.OK)
    }
}