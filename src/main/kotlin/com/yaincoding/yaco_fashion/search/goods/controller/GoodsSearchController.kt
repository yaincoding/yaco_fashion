package com.yaincoding.yaco_fashion.search.goods.controller

import com.yaincoding.yaco_fashion.search.goods.document.GoodsDocument
import com.yaincoding.yaco_fashion.search.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.service.GoodsSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/goods")
class GoodsSearchController (
    private val service: GoodsSearchService
) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): ResponseEntity<GoodsDocument> {
        val response: GoodsDocument? = service.getById(id)

        response?.let {
            return ResponseEntity.ok(response)
        }

        return ResponseEntity.notFound().build()
    }

    @GetMapping("/search")
    fun search(requestDto: SearchGoodsRequestDto): ResponseEntity<SearchGoodsResponseDto> {
        return ResponseEntity.ok(service.search(requestDto))
    }
}