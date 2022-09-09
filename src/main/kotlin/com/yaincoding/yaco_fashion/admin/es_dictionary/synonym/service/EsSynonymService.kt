package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.service

import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymListDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity.EsSynonym
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.repository.EsSynonymRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class EsSynonymService(
    private val repository: EsSynonymRepository
) {
    fun list(page: Int, size: Int): EsSynonymListDto {
        val pageable: Pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id")
        val esSynonymPage: Page<EsSynonym> = repository.findAll(pageable)
        val esSynonymList: List<EsSynonym> = esSynonymPage.get().toList()
        return EsSynonymListDto(
            list = esSynonymList,
            totalCount = esSynonymPage.totalElements
        )
    }

    fun search(query: String, page: Int, size: Int): EsSynonymListDto {
        val pageable: Pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id")
        val esSynonymPage: Page<EsSynonym> = repository.findByWordOrSynonym(query, pageable)
        val esSynonymList: List<EsSynonym> = esSynonymPage.get().toList()
        return EsSynonymListDto(
            list = esSynonymList,
            totalCount = esSynonymPage.totalElements
        )
    }

    fun save(dto: EsSynonymDto): EsSynonymDto {
        val esSynonym: EsSynonym = dto.toEntity()
        val entity: EsSynonym = repository.save(esSynonym)
        return entity.toDto()
    }

    fun update(id: Long, dto: EsSynonymDto): EsSynonymDto {
        val esSynonym: EsSynonym = repository.findById(id).orElseThrow()

        dto.word?.let {
            esSynonym.word = it
        }

        dto.synonym?.let {
            esSynonym.synonym = it
        }

        esSynonym.active = dto.active

        val entity: EsSynonym = repository.save(esSynonym)

        return entity.toDto()
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }
}