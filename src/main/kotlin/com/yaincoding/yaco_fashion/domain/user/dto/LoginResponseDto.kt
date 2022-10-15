package com.yaincoding.yaco_fashion.domain.user.dto

data class LoginResponseDto (
    val name: String,
    val picture: String,
    val accessToken: String,
    val refreshToken: String,
)