package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto

import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity.EsSynonym

data class EsSynonymListDto (

    val list: List<EsSynonym> = listOf(),
    val totalCount: Long = 0L
)
