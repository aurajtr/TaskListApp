package com.example.tracklist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val category: String = "",
    val dueDate: Timestamp? = null,
    val priority: Int = 0
)