package com.yaincoding.yaco_fashion.domain.goods.controller

import com.yaincoding.yaco_fashion.domain.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.domain.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.domain.goods.dto.SearchGoodsResponseDto
import com.yaincoding.yaco_fashion.domain.goods.service.GoodsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class GoodsController (
    @Autowired val goodsService: GoodsService
) {

    @GetMapping("/goods/{id}")
    fun getById(@PathVariable id: Int): ResponseEntity<GetGoodsResponseDto> {
        val response: GetGoodsResponseDto? = goodsService.getById(id)
        response?.let {
            return ResponseEntity.ok(response)
        }

        return ResponseEntity.notFound().build()
    }

    @GetMapping("/goods/search")
    fun search(requestDto: SearchGoodsRequestDto): ResponseEntity<SearchGoodsResponseDto> {
        return ResponseEntity.ok(goodsService.search(requestDto))
    }
}