package com.yaincoding.yaco_fashion.domain.user.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.yaincoding.yaco_fashion.domain.user.domain.Role
import com.yaincoding.yaco_fashion.domain.user.domain.User
import com.yaincoding.yaco_fashion.domain.user.dto.GoogleIdTokenRequestDto
import com.yaincoding.yaco_fashion.domain.user.dto.JwtDto
import com.yaincoding.yaco_fashion.domain.user.dto.LoginResponseDto
import com.yaincoding.yaco_fashion.domain.user.jwt.JwtProvider
import com.yaincoding.yaco_fashion.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {

    fun loginOAuthGoogle(idTokenRequestDto: GoogleIdTokenRequestDto): LoginResponseDto {
        val idTokenVerifier: GoogleIdTokenVerifier =
            GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
                .setAudience(Collections.singletonList(idTokenRequestDto.clientId))
                .build()

        val idTokenObject: GoogleIdToken = idTokenVerifier.verify(idTokenRequestDto.credential)
        val payload: GoogleIdToken.Payload = idTokenObject.payload

        val email: String = payload.email
        val name: String = payload["name"] as String
        val picture: String = payload["picture"] as String

        saveOrUpdate(email = email, name = name, picture = picture)

        val jwtDto: JwtDto = jwtProvider.generateJwtDto(email)

        return LoginResponseDto(
            name = name,
            picture = picture,
            accessToken = jwtDto.accessToken,
            refreshToken = jwtDto.refreshToken,
        )
    }

    private fun saveOrUpdate(email: String, name: String, picture: String) {

        userRepository.findByEmail(email)?.let {
            it.picture = picture
            userRepository.save(it)
        } ?: run {
            userRepository.save(
                User(
                    email = email,
                    name = name,
                    picture = picture,
                    role = Role.ROLE_USER
                )
            )
        }
    }
}