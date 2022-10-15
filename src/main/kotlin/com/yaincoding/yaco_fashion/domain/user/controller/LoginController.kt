package com.yaincoding.yaco_fashion.domain.user.controller

import com.yaincoding.yaco_fashion.domain.user.dto.GoogleIdTokenRequestDto
import com.yaincoding.yaco_fashion.domain.user.dto.JwtDto
import com.yaincoding.yaco_fashion.domain.user.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(
    private val authService: AuthService
) {

    @PostMapping("/api/login")
    fun login(@RequestBody requestDto: GoogleIdTokenRequestDto): ResponseEntity<JwtDto> {
        val jwtDto: JwtDto = authService.loginOAuthGoogle(requestDto);
        return ResponseEntity.ok(jwtDto)
    }
}