package com.yaincoding.yaco_fashion.admin.dictionary.goods

import com.yaincoding.yaco_fashion.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "goods_word")
class GoodsWordDictionary: BaseTime() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(name = "word", nullable = false)
    var word: String? = null

    @Column(name = "expression", nullable = true)
    var expression: String? = null

    @Column(name = "active", nullable = false)
    var active: Boolean = true
}