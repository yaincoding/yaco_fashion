package com.yaincoding.yaco_fashion.domain.top_keyword.entity

import com.yaincoding.yaco_fashion.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "top_keyword")
class TopKeyword (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "keyword", nullable = false)
    var keyword: String
): BaseTime()