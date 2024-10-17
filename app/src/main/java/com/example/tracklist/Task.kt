package com.example.tracklist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val category: String = "",
    val dueDate: Timestamp? = null,
    val priority: Int = 0
)