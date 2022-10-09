package com.yaincoding.yaco_fashion.admin.es_index.goods.dto

data class CreateIndexResponseDto (
    val acknowledged: Boolean,
    val shardsAcknowledged: Boolean,
    val index: String,
)
