package com.yaincoding.yaco_fashion.domain.user.domain

import javax.persistence.*

@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "name")
    var name: String,

    @Column(name = "picture")
    var picture: String,

    @Enumerated(EnumType.STRING)
    var role: Role
)