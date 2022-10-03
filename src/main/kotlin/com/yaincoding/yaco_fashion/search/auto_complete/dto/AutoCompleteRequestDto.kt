package com.yaincoding.yaco_fashion.search.auto_complete.dto

data class AutoCompleteRequestDto (
    val query: String,
    val size: Int = 5,
)
