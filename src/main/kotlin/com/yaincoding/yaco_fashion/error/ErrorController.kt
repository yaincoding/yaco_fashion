package com.yaincoding.yaco_fashion.error

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class CustomErrorController: ErrorController {

    @RequestMapping("/error")
    fun handleError(): String {
        return "/index.html";
    }

    @Override
    fun getErrorPath(): String {
        return "/error";
    }

}