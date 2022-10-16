package com.yaincoding.yaco_fashion.domain.user.jwt

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val bearerToken: String? = request.getHeader(HttpHeaders.AUTHORIZATION)

        bearerToken?.let {
            val jwt = jwtProvider.resolveToken(bearerToken)
            try {
                if (StringUtils.hasText(jwt) && jwtProvider.validateAccessToken(jwt)) {
                    val authentication = jwtProvider.getAuthentication(jwt)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (e: ExpiredJwtException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value())
            }
        }

        filterChain.doFilter(request, response)
    }
}