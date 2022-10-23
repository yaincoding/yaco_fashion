package com.yaincoding.yaco_fashion.admin.es_dictionary.word.controller

import com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto.EsWordListDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.word.dto.EsWordDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.word.service.EsWordService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/es_word")
class EsWordController(
    private val service: EsWordService
) {

    @GetMapping("/list")
    fun list(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "0") size: Int
    ): ResponseEntity<EsWordListDto> {
        val responseDto: EsWordListDto = service.list(page=page, size=size)
        return ResponseEntity.ok(responseDto)
    }

    @GetMapping("/search")
    fun search(
        @RequestParam(required = true) query: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "0") size: Int
    ): ResponseEntity<EsWordListDto> {
        val responseDto: EsWordListDto = service.search(query=query, page=page, size=size)
        return ResponseEntity.ok(responseDto)
    }

    @PostMapping("/save")
    fun save(@RequestBody dto: EsWordDto): ResponseEntity<EsWordDto> {
        val responseDto: EsWordDto = service.save(dto)
        return ResponseEntity.ok(responseDto)
    }

    @PutMapping("/update/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: EsWordDto): ResponseEntity<EsWordDto> {
        val responseDto: EsWordDto = service.update(id, dto)
        return ResponseEntity.ok(responseDto)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<HttpStatus> {
        service.delete(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/upload_file")
    fun uploadFile(): ResponseEntity<HttpStatus> {
        service.uploadToS3()
        return ResponseEntity(HttpStatus.OK)
    }
}