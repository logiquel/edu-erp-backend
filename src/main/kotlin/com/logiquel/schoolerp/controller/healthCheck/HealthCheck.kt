package com.logiquel.schoolerp.controller.healthCheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/healthcheck"])
class HealthCheck {

    @GetMapping
    fun healthCheck(): String {
        return "OK"
    }
}