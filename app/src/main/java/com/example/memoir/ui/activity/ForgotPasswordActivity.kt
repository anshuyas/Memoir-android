package com.example.memoir.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.memoir.databinding.ActivityForgotPasswordBinding
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.viewmodel.UserViewModel
import com.example.memoir.viewmodel.UserViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val userViewModel: UserViewModel by viewModels{
        UserViewModelFactory(UserRepositoryImpl())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Reset Password button click listener
        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                return@setOnClickListener
            }

            // Call the ViewModel method to send a password reset email
            userViewModel.forgetPassword(email)
        }

        // Observe LiveData for error messages
        userViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe LiveData for password reset success
        userViewModel.isPasswordResetSuccessful.observe(this) { isSuccessful ->
            if (isSuccessful) {
                Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}