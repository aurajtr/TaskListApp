package com.example.tracklist.util

import com.example.tracklist.model.Task

object TaskValidator {
    fun validateTask(task: Task): ValidationResult {
        return when {
            task.title.isBlank() -> ValidationResult.Error("Title cannot be empty")
            task.title.length > 50 -> ValidationResult.Error("Title cannot exceed 50 characters")
            task.description.length > 200 -> ValidationResult.Error("Description cannot exceed 200 characters")
            task.priority !in 0..2 -> ValidationResult.Error("Invalid priority value")
            else -> ValidationResult.Success
        }
    }
}