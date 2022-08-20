package com.yaincoding.yaco_fashion.domain.goods.service

import com.yaincoding.yaco_fashion.domain.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.domain.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.domain.goods.dto.SearchGoodsResponseDto

interface GoodsService {
    fun getById(id: Int): GetGoodsResponseDto?
    fun search(requestDto: SearchGoodsRequestDto): SearchGoodsResponseDto
}
