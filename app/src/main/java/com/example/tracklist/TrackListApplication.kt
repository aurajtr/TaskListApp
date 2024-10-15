package com.example.tracklist

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class TrackListApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}