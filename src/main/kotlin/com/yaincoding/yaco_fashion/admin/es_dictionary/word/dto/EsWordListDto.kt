package com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto

import com.yaincoding.yaco_fashion.admin.es_dictionary.word.entity.EsWord

data class EsWordListDto (

    val list: List<EsWord> = listOf(),
    val totalCount: Long = 0L
)
