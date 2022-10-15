package com.yaincoding.yaco_fashion.domain.user.dto

data class JwtDto(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)
