package com.yaincoding.yaco_fashion.domain.goods.entity

import com.yaincoding.yaco_fashion.common.BaseTime
import com.yaincoding.yaco_fashion.domain.category.entity.Category
import javax.persistence.*

@Entity
@Table(name = "goods")
class Goods (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @JoinColumn(name = "category_id", nullable = false)
    @ManyToOne(targetEntity = Category::class, fetch = FetchType.EAGER)
    var category: Category,

    @Column(name = "image_url", nullable = true)
    var imageUrl: String? = null,

    @Column(name = "click_count", nullable = false)
    var clickCount: Int = 0,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @Column(name = "sell_count", nullable = false)
    var sellCount: Int = 0,

    @Column(name = "gender", nullable = true)
    var gender: String? = null,

    @Column(name = "hash_tags", nullable = true)
    var hashTags: String? = null,

    @Column(name = "price", nullable = false)
    var price: Int = 0,

    @Column(name = "link", nullable = true)
    var link: String? = null,

): BaseTime()