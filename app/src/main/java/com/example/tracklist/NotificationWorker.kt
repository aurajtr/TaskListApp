package com.example.tracklist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: return Result.failure()

        val now = Timestamp.now()
        val twentyFourHoursLater = Timestamp(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))

        val tasks = firestore.collection("users").document(currentUser.uid).collection("tasks")
            .whereGreaterThan("dueDate", now)
            .whereLessThan("dueDate", twentyFourHoursLater)
            .whereEqualTo("isCompleted", false)
            .get()
            .await()
            .toObjects(Task::class.java)

        for (task in tasks) {
            showNotification(task)
        }

        return Result.success()
    }

    private fun showNotification(task: Task) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_notifications",
                "Task Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "task_notifications")
            .setContentTitle("Task Due Soon")
            .setContentText("${task.title} is due within 24 hours")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(task.id.hashCode(), notification)
    }
}