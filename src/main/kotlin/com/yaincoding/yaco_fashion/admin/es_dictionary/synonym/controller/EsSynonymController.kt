package com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.controller

import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymListDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.dto.EsSynonymDto
import com.yaincoding.yaco_fashion.admin.es_dictionary.synonym.service.EsSynonymService
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
@RequestMapping("/api/es_synonym")
class EsSynonymController(
    private val service: EsSynonymService
) {

    @GetMapping("/list")
    fun list(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "0") size: Int
    ): ResponseEntity<EsSynonymListDto> {
        val responseDto: EsSynonymListDto = service.list(page=page, size=size)
        return ResponseEntity.ok(responseDto)
    }

    @GetMapping("/search")
    fun search(
        @RequestParam(required = true) query: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "0") size: Int
    ): ResponseEntity<EsSynonymListDto> {
        val responseDto: EsSynonymListDto = service.search(query=query, page=page, size=size)
        return ResponseEntity.ok(responseDto)
    }

    @PostMapping("/save")
    fun save(@RequestBody requestDto: EsSynonymDto): ResponseEntity<EsSynonymDto> {
        val responseDto: EsSynonymDto = service.save(requestDto)
        return ResponseEntity.ok(responseDto)
    }

    @PutMapping("/update/{id}")
    fun update(@PathVariable id: Long, @RequestBody requestDto: EsSynonymDto): ResponseEntity<EsSynonymDto> {
        val responseDto: EsSynonymDto = service.update(id, requestDto)
        return ResponseEntity.ok(responseDto)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<HttpStatus> {
        service.delete(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/apply")
    fun apply(): ResponseEntity<HttpStatus> {
        service.updatePackage()
        return ResponseEntity(HttpStatus.OK)
    }
}