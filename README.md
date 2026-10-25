# Spring Boot Kotlin Gemini AI Engine

Un backend robusto y reactivo desarrollado con Spring Boot 3, Kotlin 1.9 y JDK 21 para integrar las capacidades multimodales de Google Gemini AI mediante Spring AI.

Permite procesar tareas de texto y análisis/transcripción de audio (.mp3, .m4a) de forma síncrona y asíncrona, gestionando operaciones pesadas en segundo plano mediante Kotlin Coroutines y persistiendo el ciclo de vida de cada tarea en H2 Database.

---

## Características Clave

* **Integración Multimodal:** Procesamiento directo de texto y archivos de audio (MP3, WAV, M4A) con Gemini sin necesidad de motores Speech-to-Text externos.
* **Procesamiento Asíncrono:** Uso de Corrutinas de Kotlin (Dispatchers.IO) para tareas no bloqueantes con respuestas 202 Accepted inmediatas.
* **Persistencia de Estado:** Trazabilidad completa (PENDING, PROCESSING, COMPLETED, FAILED) con Spring Data JPA y H2 Database.
* **Compatibilidad Kotlin-JPA:** Configurado con valores por defecto en entidades para garantizar la instanciación por reflexión de Hibernate sin constructores por defecto.
* **Optimización de Tokens:** Control de temperatura y límite de tokens de salida (maxOutputTokens) para respuestas concisas y directas.
* **Documentación Interactiva:** Interfaz Swagger UI integrada para pruebas de la API.

---

## Tecnologías Utilizadas

* **Lenguaje:** Kotlin 1.9 / Java 21
* **Framework:** Spring Boot 3.2+ (Spring AI, Spring Data JPA, Spring Web)
* **Base de Datos:** H2 Database (En memoria)
* **Concurrencia:** Kotlin Coroutines
* **Documentación:** OpenAPI 3 / Swagger UI

---

## Configuración y Ejecución

### 1. Requisitos Previos
* JDK 21 instalado.
* API Key activa de Google AI Studio (aistudio.google.com).

### 2. Variables de Entorno
Configura el archivo `src/main/resources/application.properties` con tu clave de API y la ruta de la consola H2:

```properties
# Clave de API de Gemini
spring.ai.google.genai.api-key=TU_API_KEY_AQUI

# Ajustes de concisidad y límites
spring.ai.google.genai.chat.options.max-output-tokens=300
spring.ai.google.genai.chat.options.temperature=0.2

# Consola Web H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=false

# Configuración de Base de Datos
spring.datasource.url=jdbc:h2:mem:aidb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
