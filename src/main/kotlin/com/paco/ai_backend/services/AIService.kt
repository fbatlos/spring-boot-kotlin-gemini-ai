package com.paco.ai_backend.services

import com.paco.ai_backend.db.entities.TaskEntity
import com.paco.ai_backend.db.entities.TaskStatus
import com.paco.ai_backend.db.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.ai.chat.client.ChatClient
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class AiService(private val chatClientBuilder: ChatClient.Builder, private  val taskRepository: TaskRepository) {

    private val chatClient = chatClientBuilder.build()

    // Consulta inmediata
    fun askQuickQuestion(prompt: String): String {
        return chatClient.prompt(prompt)
            .system("Eres un asistente técnico conciso. Responde siempre de forma breve, directa y estructurada, evitando introducciones innecesarias.")
            .call()
            .content() ?: "Sin respuesta."
    }

    // Tarea asíncrona pesada en DB
    fun processHeavyTask(prompt: String): String {
        val jobId = UUID.randomUUID().toString()

        //Guardamos la tarea en estado pendiente
        val task = TaskEntity(
            id = jobId,
            prompt = prompt,
            status = TaskStatus.PENDING
        )
        taskRepository.save(task)

        //Ejecutamos la sincronización
        CoroutineScope(Dispatchers.IO).launch {
            try {
                task.status = TaskStatus.PENDING
                task.updatedAt = LocalDateTime.now()
                taskRepository.save(task)

                //GEMINI
                val result = chatClient.prompt(prompt)
                    .system("Eres un asistente técnico conciso. Responde siempre de forma breve, directa y estructurada, evitando introducciones innecesarias.")
                    .call().content() ?: "No datos..."

                task.status = TaskStatus.COMPLETED
                task.result = result
                task.updatedAt = LocalDateTime.now()
                taskRepository.save(task)


            } catch (e: Exception) {
                // Guardar fallo en caso de excepción
                task.status = TaskStatus.FAILED
                task.result = "Error: ${e.message}"
                task.updatedAt = LocalDateTime.now()
                taskRepository.save(task)
            }
        }
        return jobId
    }

    fun processAudioAsync(audioBytes: ByteArray, filename: String, mimeType: String, instructions: String): String {
        val jobId = UUID.randomUUID().toString()

        // 1. Guardar la tarea inicial en estado PENDING
        val task = TaskEntity(
            id = jobId,
            prompt = "Audio: $filename | Instrucción: $instructions",
            status = TaskStatus.PENDING
        )
        taskRepository.save(task)

        // 2. Ejecutar la llamada asíncrona en el Dispatcher IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Actualizar estado a PROCESSING
                task.status = TaskStatus.PROCESSING
                task.updatedAt = LocalDateTime.now()
                taskRepository.save(task)

                // Crear el recurso de audio a partir de los bytes
                val audioResource = object : ByteArrayResource(audioBytes) {
                    override fun getFilename(): String = filename
                }
                val parsedMimeType = MimeTypeUtils.parseMimeType(mimeType)

                // Llamada multimodal a Gemini
                val result = chatClient.prompt()
                    .user { userSpec ->
                        userSpec
                            .text(instructions)
                            .media(parsedMimeType, audioResource)
                    }
                    .call()
                    .content() ?: "No se pudo extraer información del archivo de audio."

                // Actualizar estado a COMPLETED
                task.status = TaskStatus.COMPLETED
                task.result = result
                task.updatedAt = LocalDateTime.now()
                taskRepository.save(task)


            } catch (e: Exception) {
                // Registrar fallo en caso de error
                task.status = TaskStatus.FAILED
                task.result = "Error al procesar audio: ${e.message}"
                task.updatedAt = LocalDateTime.now()
                taskRepository.save(task)
            }
        }

        return jobId
    }

    fun getJobStatus(jobId: String): TaskEntity? {
        return taskRepository.findById(jobId).orElse(null)
    }
}