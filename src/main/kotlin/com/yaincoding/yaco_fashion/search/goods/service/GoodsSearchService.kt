package com.yaincoding.yaco_fashion.search.goods.service

import com.yaincoding.yaco_fashion.search.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsResponseDto

interface GoodsSearchService {
    fun getById(id: Int): GetGoodsResponseDto?
    fun search(requestDto: SearchGoodsRequestDto): SearchGoodsResponseDto
}
