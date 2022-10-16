package com.yaincoding.yaco_fashion.config.security

import com.yaincoding.yaco_fashion.domain.user.jwt.JwtFilter
import com.yaincoding.yaco_fashion.domain.user.jwt.JwtProvider
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtProvider: JwtProvider
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .csrf {
                it.disable()
            }
            .httpBasic {
                it.disable()
            }
            .authorizeRequests {
                it.antMatchers("/static/css/**", "/static/js/**", "/static/images/**").permitAll()
                it.antMatchers().authenticated()
                it.antMatchers("/api/login").permitAll()
                it.antMatchers(HttpMethod.POST).hasAnyRole("SUPER", "ADMIN")
                it.antMatchers(HttpMethod.PUT).hasAnyRole("SUPER", "ADMIN")
                it.antMatchers(HttpMethod.DELETE).hasAnyRole("SUPER", "ADMIN")
                it.antMatchers("/api/index/**").hasRole("SUPER")
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.NEVER)
            }
            .addFilterBefore(JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
    }
}