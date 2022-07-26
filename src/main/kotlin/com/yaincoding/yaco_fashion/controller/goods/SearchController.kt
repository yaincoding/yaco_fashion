package com.yaincoding.yaco_fashion.controller.goods

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SearchController {

    @GetMapping("/hello")
    fun goodsList(): List<String> {
        return listOf("hi", "hello")
    }
}