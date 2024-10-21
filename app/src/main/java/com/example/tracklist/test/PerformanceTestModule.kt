package com.example.tracklist.test

import android.util.Log
import com.example.tracklist.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerformanceTestModule(
    private val firestoreRepo: TaskRepository,
    private val roomRepo: TaskRepository
) {
    suspend fun runTests() {
        testInsert()
        testRead()
        testUpdate()
        testDelete()
    }

    private suspend fun testInsert() = withContext(Dispatchers.Default) {
        val task = Task(title = "Test Task", description = "Test Description")

        val firestoreTime = measureTime { firestoreRepo.insertTask(task) }
        Log.d("PerformanceTest", "Firestore Insert Time: $firestoreTime ms")

        val roomTime = measureTime { roomRepo.insertTask(task) }
        Log.d("PerformanceTest", "Room Insert Time: $roomTime ms")
    }

    private suspend fun testRead() = withContext(Dispatchers.Default) {
        val firestoreTime = measureTime { firestoreRepo.getAllTasks() }
        Log.d("PerformanceTest", "Firestore Read Time: $firestoreTime ms")

        val roomTime = measureTime { roomRepo.getAllTasks() }
        Log.d("PerformanceTest", "Room Read Time: $roomTime ms")
    }

    private suspend fun testUpdate() = withContext(Dispatchers.Default) {
        val task = Task(id = "1", title = "Updated Task", description = "Updated Description")

        val firestoreTime = measureTime { firestoreRepo.updateTask(task) }
        Log.d("PerformanceTest", "Firestore Update Time: $firestoreTime ms")

        val roomTime = measureTime { roomRepo.updateTask(task) }
        Log.d("PerformanceTest", "Room Update Time: $roomTime ms")
    }

    private suspend fun testDelete() = withContext(Dispatchers.Default) {
        val taskId = "1"

        val firestoreTime = measureTime { firestoreRepo.deleteTask(taskId) }
        Log.d("PerformanceTest", "Firestore Delete Time: $firestoreTime ms")

        val roomTime = measureTime { roomRepo.deleteTask(taskId) }
        Log.d("PerformanceTest", "Room Delete Time: $roomTime ms")
    }

    private suspend fun measureTime(block: suspend () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }
}