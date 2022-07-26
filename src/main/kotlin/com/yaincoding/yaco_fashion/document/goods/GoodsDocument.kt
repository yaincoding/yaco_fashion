package com.yaincoding.yaco_fashion.document.goods

data class GoodsDocument (
    val id: Int,
    val title: String,
    val categoryId: Int,
    val parentCategoryId: Int,
    val categoryName: String,
    val parentCategoryName: String,
    val imageUrl: String,
    val clickCount: Int,
    val sellCount: Int,
    val likeCount: Int,
    val gender: String,
    val hashTags: List<String>?,
    val price: Int,
    val link: String,
)
