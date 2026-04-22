package com.logiquel.schoolerp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedOriginPatterns = listOf("*")   // allow all origins
        config.allowedHeaders = listOf("*")           // allow all headers
        config.allowedMethods = listOf(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        )
        config.exposedHeaders = listOf(
            "Authorization",
            "Content-Type"
        )

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }
}