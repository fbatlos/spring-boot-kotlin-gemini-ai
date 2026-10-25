package com.paco.ai_backend.db.repository


import com.paco.ai_backend.db.entities.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<TaskEntity, String>