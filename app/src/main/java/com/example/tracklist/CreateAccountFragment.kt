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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
            if (validateInputs()) {
                val fullName = binding.fullNameInput.text.toString().trim()
                val email = binding.emailInput.text.toString().trim()
                val password = binding.passwordInput.text.toString()
                createAccount(fullName, email, password)
            }
        }

        binding.backToLoginLink.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInputs(): Boolean {
        val fullName = binding.fullNameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (fullName.isEmpty()) {
            binding.fullNameInputLayout.error = "Full name is required"
            return false
        }
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInputLayout.error = "Confirm password is required"
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordInputLayout.error = "Passwords do not match"
            return false
        }

        binding.fullNameInputLayout.error = null
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.confirmPasswordInputLayout.error = null

        return true
    }

    private fun createAccount(fullName: String, email: String, password: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "email" to email
                    )
                    user?.let { firebaseUser ->
                        db.collection("users").document(firebaseUser.uid).set(userData)
                            .addOnSuccessListener {
                                showSuccessMessage()
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
            is FirebaseAuthInvalidCredentialsException -> showSnackbar("Invalid email format. Please enter a valid email address.")
            is FirebaseAuthWeakPasswordException -> showSnackbar("Password is too weak. Please use a stronger password.")
            else -> showSnackbar("Account creation failed: ${exception?.message}")
        }
    }

    private fun showSuccessMessage() {
        val message = "Account created successfully. Please proceed to the login page and sign in."
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("OK") {
                val action = CreateAccountFragmentDirections.actionCreateAccountFragmentToLoginFragment(true)
                findNavController().navigate(action)
            }
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.createAccountButton.isEnabled = !isLoading
        binding.createAccountProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}