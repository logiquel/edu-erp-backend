package com.logiquel.schoolerp.controller.healthCheck

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/healthcheck"])
class HealthCheck {

    @GetMapping
    fun healthCheck(): ResponseEntity<Any> {

        try{
            return ResponseEntity.ok(   "hum pa to ha hi noooooooo")
        }
        catch (e: Exception){
            return ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }

    }
}