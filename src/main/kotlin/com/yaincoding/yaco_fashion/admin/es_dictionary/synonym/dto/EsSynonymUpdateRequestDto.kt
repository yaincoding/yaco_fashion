package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto

data class EsSynonymUpdateRequestDto (
    val id: Long,
    val word: String? = null,
    val synonym: String? = null,
    val active: Boolean = true
)

