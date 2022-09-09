package com.yaincoding.yaco_fashion.admin.es_dictionary.word.repository

import com.yaincoding.yaco_fashion.admin.es_dictionary.word.entity.EsWord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

interface EsWordRepository: PagingAndSortingRepository<EsWord, Long> {
    fun findByWordContains(query: String, pageable: Pageable): Page<EsWord>
}