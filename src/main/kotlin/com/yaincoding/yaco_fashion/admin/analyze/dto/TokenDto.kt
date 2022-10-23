package com.yaincoding.yaco_fashion.admin.analyze.dto

data class TokenDto (
    val token: String,
    val type: String,
    val position: Int,
    val startOffset: Int,
    val endOffset: Int,
)
