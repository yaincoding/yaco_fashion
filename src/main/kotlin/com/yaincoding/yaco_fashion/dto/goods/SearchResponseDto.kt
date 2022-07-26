package com.yaincoding.yaco_fashion.dto.goods

class SearchResponseDto (
    val count: Int,
    val hits: List<GetGoodsResponseDto>
)