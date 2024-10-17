package com.example.tracklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tracklist.databinding.FragmentCreateAccountBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountFragment : Fragment() {
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.createAccountButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showSnackbar("Please fill in all fields")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showSnackbar("Passwords do not match")
                return@setOnClickListener
            }

            createAccount(fullName, email, password)
        }

        binding.backToLoginLink.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun createAccount(fullName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "email" to email
                    )
                    user?.let { firebaseUser ->
                        db.collection("users").document(firebaseUser.uid).set(userData)
                            .addOnSuccessListener {
                                navigateToTaskList()
                            }
                            .addOnFailureListener { e ->
                                showSnackbar("Error saving user data: ${e.message}")
                            }
                    }
                } else {
                    handleCreateAccountError(task.exception)
                }
            }
    }

    private fun handleCreateAccountError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> showSnackbar("Email already in use. Please use a different email or try logging in.")
            else -> showSnackbar("Account creation failed: ${exception?.message}")
        }
    }

    private fun navigateToTaskList() {
        findNavController().navigate(R.id.action_createAccountFragment_to_taskListFragment)
    }

    private fun showSnackbar(message: String) {
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}