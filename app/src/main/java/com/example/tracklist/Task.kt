package com.example.tracklist

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId val id: String? = null,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val category: String = "",
    val dueDate: Timestamp? = null,
    val priority: Int = 0
)