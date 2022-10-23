package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.service

import com.amazonaws.services.s3.AmazonS3
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.controller.EsSynonymController
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymListDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity.EsSynonym
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.repository.EsSynonymRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class EsSynonymService(
    private val repository: EsSynonymRepository,
    private val s3: AmazonS3,
    @Value("\${dictionary.s3_bucket_name}") private val s3BucketName: String,
    @Value("\${dictionary.synonyms.s3_prefix}") private val s3Prefix: String,
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
        val esSynonym = EsSynonym(
            word = dto.word,
            synonym = dto.synonym,
            active = dto.active?: true,
            bidirect = dto.bidirect?: true,
        )
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

        dto.active?.let {
            esSynonym.active = dto.active
        }

        dto.bidirect?.let {
            esSynonym.bidirect = dto.bidirect
        }

        val entity: EsSynonym = repository.save(esSynonym)

        return entity.toDto()
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }

    fun uploadToS3() {

        val synonymLines: List<String> = repository
            .findAll()
            .toList()
            .map {
                if (it.bidirect) "${it.word},${it.synonym}" else "${it.word} => ${it.synonym}"
            }
        val content: String = synonymLines.joinToString("\n")

        val tz: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.KOREA).withZone(tz.toZoneId())
        val suffix: String = LocalDateTime.now().format(formatter)

        s3.putObject(s3BucketName, "${s3Prefix}/synonyms_${suffix}.txt", content)
    }
}