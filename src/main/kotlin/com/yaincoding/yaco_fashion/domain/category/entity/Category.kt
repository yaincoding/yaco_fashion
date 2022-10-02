package com.yaincoding.yaco_fashion.domain.category.entity

import com.yaincoding.yaco_fashion.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "category")
class Category (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "parent_id", nullable = true)
    var parent_id: Long? = null,
): BaseTime()