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
            val fullName = binding.fullNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userData = hashMapOf(
                                "fullName" to fullName,
                                "email" to email
                            )
                            user?.let {
                                db.collection("users").document(it.uid).set(userData)
                                    .addOnSuccessListener {
                                        findNavController().navigate(R.id.action_createAccountFragment_to_taskListFragment)
                                    }
                                    .addOnFailureListener { e ->
                                        Snackbar.make(view, "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Snackbar.make(view, "Account creation failed.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Snackbar.make(view, "Please fill in all fields and ensure passwords match", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.backToLoginLink.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}