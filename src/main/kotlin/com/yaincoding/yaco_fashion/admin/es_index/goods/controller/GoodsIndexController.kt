package com.yaincoding.yaco_fashion.admin.es_index.goods.controller

import com.yaincoding.yaco_fashion.admin.es_index.goods.service.GoodsIndexService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/index/goods")
class GoodsIndexController(
    private val service: GoodsIndexService
) {

    @GetMapping("/reindex")
    fun reindex(): ResponseEntity<HttpStatus> {
        service.reindex()
        return ResponseEntity(HttpStatus.OK)
    }
}