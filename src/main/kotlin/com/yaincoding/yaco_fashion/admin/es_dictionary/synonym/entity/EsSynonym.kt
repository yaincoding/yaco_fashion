package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity

import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymDto
import com.yaincoding.yaco_fashion.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "es_synonym")
class EsSynonym (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "word", nullable = false)
    var word: String? = null,

    @Column(name = "synonym", nullable = false)
    var synonym: String? = null,

    @Column(name = "active", nullable = false)
    var active: Boolean = true

): BaseTime() {

    fun toDto(): EsSynonymDto {
        return EsSynonymDto(
            word = word,
            synonym = synonym,
            active = active,
        )
    }
}