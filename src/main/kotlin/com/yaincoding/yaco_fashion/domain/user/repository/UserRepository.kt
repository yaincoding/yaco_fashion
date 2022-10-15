package com.yaincoding.yaco_fashion.domain.user.repository

import com.yaincoding.yaco_fashion.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String) : User?
}