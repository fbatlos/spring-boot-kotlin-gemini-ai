package com.paco.ai_backend.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Gemini AI Integration API")
                    .version("1.0.0")
                    .description("Backend en Kotlin y Spring Boot para la integración síncrona y asíncrona (Corrutinas) con Google Gemini AI.")
            )
    }
}