package com.example.tracklist.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tracklist.databinding.ActivityPerformanceTestBinding
import kotlinx.coroutines.launch

class PerformanceTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerformanceTestBinding
    private lateinit var testModule: PerformanceTestModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerformanceTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firestoreRepo = FirestoreTaskRepository()
        val roomRepo = RoomTaskRepository(this)
        testModule = PerformanceTestModule(firestoreRepo, roomRepo)

        binding.btnRunTests.setOnClickListener {
            runTests()
        }
    }

    private fun runTests() {
        lifecycleScope.launch {
            binding.tvResults.text = "Running tests...\n"
            testModule.runTests()
            binding.tvResults.append("Tests completed. Check Logcat for results.")
        }
    }
}