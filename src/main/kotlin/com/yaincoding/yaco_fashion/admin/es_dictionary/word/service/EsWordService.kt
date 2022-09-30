package com.yaincoding.yaco_fashion.admin.es_dictionary.word.service

import com.amazonaws.services.s3.AmazonS3
import com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto.EsWordDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto.EsWordListDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.word.entity.EsWord
import com.yaincoding.yaco_fashion.admin.es_dictionary.word.repository.EsWordRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class EsWordService(
    private val repository: EsWordRepository,
    private val s3: AmazonS3,
    @Value("\${dictionary.s3_bucket_name}") private val s3BucketName: String,
    @Value("\${dictionary.words.s3_key}") private val s3Key: String,
) {
    fun list(page: Int, size: Int): EsWordListDto {
        val pageable: Pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id")
        val esWordPage: Page<EsWord> = repository.findAll(pageable)
        return EsWordListDto(
            list = esWordPage.get().toList(),
            totalCount = esWordPage.totalElements
        )
    }

    fun search(query: String, page: Int, size: Int): EsWordListDto {
        val pageable: Pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id")
        val esWordPage: Page<EsWord> = repository.findByWordContains(query, pageable)
        return EsWordListDto(
            list = esWordPage.get().toList(),
            totalCount = esWordPage.totalElements
        )
    }

    fun save(dto: EsWordDto): EsWordDto {
        val esWord: EsWord = dto.toEntity()
        val entity: EsWord = repository.save(esWord)
        return entity.toDto()
    }

    fun update(id: Long, dto: EsWordDto): EsWordDto {
        val esWord: EsWord = repository.findById(id).orElseThrow()

        dto.word?.let {
            esWord.word = it
        }

        dto.expression?.let {
            esWord.expression = it
        }

        esWord.active = dto.active

        val entity: EsWord = repository.save(esWord)

        return entity.toDto()
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }

    fun uploadToS3(content: String): Boolean {
        return try {
            s3.putObject(s3BucketName, s3Key, content)
            true
        } catch(e: Exception) {
            false
        }
    }
}