package com.example.tracklist.util

sealed class ValidationResult {
    object Success : ValidationResult()
    class Error(val message: String) : ValidationResult()
}