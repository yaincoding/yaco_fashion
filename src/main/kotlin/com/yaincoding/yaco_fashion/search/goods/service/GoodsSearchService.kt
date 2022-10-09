package com.yaincoding.yaco_fashion.search.goods.service

import com.yaincoding.yaco_fashion.search.goods.document.GoodsDocument
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsResponseDto

interface GoodsSearchService {
    fun getById(id: Int): GoodsDocument?
    fun search(requestDto: SearchGoodsRequestDto): SearchGoodsResponseDto
}
