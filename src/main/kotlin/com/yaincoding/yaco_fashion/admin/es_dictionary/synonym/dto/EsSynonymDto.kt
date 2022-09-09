package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto

import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity.EsSynonym

data class EsSynonymDto (
    val word: String? = null,
    val synonym: String? = null,
    val active: Boolean = true
) {
    fun toEntity(): EsSynonym {
        return EsSynonym(
            word = word,
            synonym = synonym,
            active = active,
        )
    }
}
