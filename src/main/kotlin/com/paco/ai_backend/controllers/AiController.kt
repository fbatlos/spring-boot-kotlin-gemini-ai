package com.paco.ai_backend.controllers

import com.paco.ai_backend.services.AiService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Tag(name = "AI Operations", description = "Endpoints para interactuar con Google Gemini AI")
@RestController
@RequestMapping("/api/v1/ai")
class AiController(private val aiService: AiService) {

    @Operation(summary = "Petición rápida", description = "Envía un prompt a Gemini y devuelve la respuesta de forma síncrona.")
    @PostMapping("/quick-ask")
    fun quickAsk(@RequestBody request: PromptRequest): ResponseEntity<Map<String, String>> {
        val answer = aiService.askQuickQuestion(request.prompt)
        return ResponseEntity.ok(mapOf("response" to answer))
    }

    @Operation(summary = "Iniciar tarea pesada", description = "Inicia un trabajo asíncrono procesado por Kotlin Coroutines. Devuelve inmediatamente un jobId.")
    @PostMapping("/heavy-task")
    fun startHeavyTask(@RequestBody request: PromptRequest): ResponseEntity<Map<String, String>> {
        val jobId = aiService.processHeavyTask(request.prompt)
        return ResponseEntity.accepted().body(mapOf("jobId" to jobId))
    }

    @Operation(summary = "Consultar estado de tarea pesada", description = "Comprueba si una tarea asíncrona ha finalizado mediante su jobId.")
    @GetMapping("/heavy-task/{jobId}")
    fun getJobResult(@PathVariable jobId: String): ResponseEntity<Any> {
        val task = aiService.getJobStatus(jobId)
        return if (task != null) {
            ResponseEntity.ok(task)
        } else {
            ResponseEntity.status(404).body(mapOf("error" to "Tarea no encontrada con ID: $jobId"))
        }
    }

    @Operation(
        summary = "Iniciar procesamiento asíncrono de audio",
        description = "Sube un archivo de audio (MP3, WAV, etc.) para procesarlo en segundo plano. Devuelve un jobId inmediatamente."
    )
    @PostMapping("/heavy-audio-task", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun startAudioTask(
        @RequestPart("file") file: MultipartFile,
        @RequestParam("instructions", defaultValue = "Transcribe este audio detalladamente.") instructions: String
    ): ResponseEntity<Map<String, String>> {

        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "El archivo de audio no puede estar vacío."))
        }

        val mimeType = file.contentType ?: "audio/mp3"
        val filename = file.originalFilename ?: "audio.mp3"

        // Leemos los bytes para pasárselos a la corrutina
        val jobId = aiService.processAudioAsync(file.bytes, filename, mimeType, instructions)

        return ResponseEntity.accepted().body(
            mapOf(
                "jobId" to jobId,
                "message" to "Procesamiento de audio iniciado en segundo plano."
            )
        )
    }
}

data class PromptRequest(val prompt: String = "")