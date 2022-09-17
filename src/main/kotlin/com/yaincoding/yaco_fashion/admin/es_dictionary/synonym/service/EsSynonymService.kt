package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.service

import com.amazonaws.services.s3.AmazonS3
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymListDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.entity.EsSynonym
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.repository.EsSynonymRepository
import com.yaincoding.yaco_fashion.admin.es_dictionary.utils.OpensearchPackageManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class EsSynonymService(
    private val repository: EsSynonymRepository,
    private val packageManager: OpensearchPackageManager,
    private val s3: AmazonS3,
    @Value("\${dictionary.s3_bucket_name}") private val s3BucketName: String,
    @Value("\${dictionary.synonyms.s3_key}") private val s3Key: String,
    @Value("\${dictionary.synonyms.package_name}") private val packageName: String,
    @Value("\${opensearch_domain_name}") private val domainName : String,
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

    fun uploadToS3(content: String): Boolean {
        return try {
            s3.putObject(s3BucketName, s3Key, content)
            true
        } catch(e: Exception) {
            false
        }
    }

    fun updatePackage() {
        val esSynonyms: List<String> = repository.findAll().toList().filter { e -> e.active }.map { e -> "${e.word} => ${e.synonym}" }
        val content: String = esSynonyms.joinToString("\n")

        uploadToS3(content)

        var packageId: String? = packageManager.getPackageId(packageName = packageName)
        if (packageId == null) {
            packageId = packageManager.createPackage(s3BucketName, packageName, s3Key)
        } else {
            packageManager.update(s3BucketName, packageId, s3Key)
        }

        val availablePackageVersion: String? = packageManager.waitForUpdate(packageId)

        if (availablePackageVersion != null) {
            packageManager.associate(packageId, domainName)
            packageManager.waitForAssociate(packageId, domainName, availablePackageVersion)
        }
    }
}