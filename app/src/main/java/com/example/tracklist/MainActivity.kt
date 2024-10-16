package com.example.tracklist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.*
import com.example.tracklist.databinding.ActivityMainBinding
import com.google.android.gms.safetynet.SafetyNet
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("Firebase", "Failed to initialize Firebase: ${e.message}")
            Toast.makeText(this, "Failed to initialize Firebase: ${e.message}", Toast.LENGTH_LONG).show()
        }


        // Set up the toolbar
        setSupportActionBar(binding.toolbar)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        scheduleNotificationWorker()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "task_notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }

    // Example function to create a new user
    fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "createUserWithEmail:success")
                    val user = auth.currentUser
                    // Do something with the new user
                } else {
                    Log.w("Auth", "createUserWithEmail:failure", task.exception)
                    when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(this, "Password is too weak.", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthUserCollisionException -> Toast.makeText(this, "This email is already in use.", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    // Example function to check Firestore connection
    private fun checkFirestoreConnection() {
        db.collection("test").document("test")
            .get()
            .addOnSuccessListener {
                Toast.makeText(this, "Firestore connection successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firestore connection failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}