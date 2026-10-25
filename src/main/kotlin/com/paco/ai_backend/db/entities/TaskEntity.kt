package com.paco.ai_backend.db.entities

import jakarta.persistence.*
import java.time.LocalDateTime

enum class TaskStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}

@Entity
@Table(name = "ai_tasks")
class TaskEntity(
    @Id
    val id: String = "",

    @Column(columnDefinition = "TEXT")
    val prompt: String ="",

    @Enumerated(EnumType.STRING)
    var status: TaskStatus = TaskStatus.PENDING,

    @Column(columnDefinition = "TEXT")
    var result: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)