package com.yaincoding.yaco_fashion.admin.dictionary.goods

import com.yaincoding.yaco_fashion.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "goods_synonym")
class GoodsSynonymDictionary: BaseTime() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(name = "word", nullable = false)
    var word: String? = null

    @Column(name = "synonym", nullable = false)
    var synonym: String? = null

    @Column(name = "active", nullable = false)
    var active: Boolean = true
}