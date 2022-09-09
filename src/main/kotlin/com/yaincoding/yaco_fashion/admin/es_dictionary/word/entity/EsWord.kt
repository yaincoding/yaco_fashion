package com.yaincoding.yaco_fashion.admin.es_dictionary.word.entity

import com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto.EsWordDto
import com.yaincoding.yaco_fashion.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "es_word")
class EsWord (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "word", nullable = false)
    var word: String? = null,

    @Column(name = "expression", nullable = true)
    var expression: String? = null,

    @Column(name = "active", nullable = false)
    var active: Boolean = true

): BaseTime() {

    fun toDto(): EsWordDto {
        return EsWordDto(
            word = word,
            expression = expression,
            active = active,
        )
    }
}