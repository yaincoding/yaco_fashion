package com.yaincoding.yaco_fashion.service.elasticsearch.goods

import com.yaincoding.yaco_fashion.dto.goods.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.dto.goods.SearchResponseDto

interface GoodsService {
    fun getById(id: Int): GetGoodsResponseDto
    fun search(keyword: String): SearchResponseDto
}
