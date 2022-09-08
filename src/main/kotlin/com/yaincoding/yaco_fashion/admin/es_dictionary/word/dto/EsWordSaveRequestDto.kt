package com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto

import com.yaincoding.yaco_fashion.admin.es_dictionary.word.entity.EsWord

data class EsWordSaveRequestDto (
    val word: String? = null,
    val expression: String? = null,
    val active: Boolean = true
) {
    fun toEntity(): EsWord {
        return EsWord(
            word = word,
            expression = expression,
            active = active,
        )
    }
}
