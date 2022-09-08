package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.repository

import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity.EsSynonym
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface EsSynonymRepository: PagingAndSortingRepository<EsSynonym, Long> {
    @Query(value = "SELECT * FROM es_synonym WHERE word like %:query% OR synonym like %:query%", nativeQuery = true)
    fun findByWordOrSynonym(query: String, pageable: Pageable): Page<EsSynonym>
}