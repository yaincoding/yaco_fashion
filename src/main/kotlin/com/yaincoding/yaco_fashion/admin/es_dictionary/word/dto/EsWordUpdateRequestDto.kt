package com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto

data class EsWordUpdateRequestDto (
    val id: Long,
    val word: String? = null,
    val expression: String? = null,
    val active: Boolean = true
)

