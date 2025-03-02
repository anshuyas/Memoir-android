package com.example.memoir.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.memoir.R
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.viewmodel.UserViewModel
import com.example.memoir.viewmodel.UserViewModelFactory

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val emailInput = findViewById<EditText>(R.id.email)
        val passwordInput = findViewById<EditText>(R.id.password)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Initialize ViewModel with Factory
        val repo = UserRepositoryImpl()
        val factory = UserViewModelFactory(repo)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // Observe registration success/failure
        userViewModel.isRegistrationSuccessful.observe(this) { success ->
            if (success) {
                Log.d(TAG, "Registration successful, navigating to LoginActivity")
                showToast("Registration Successful!")
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Close the current activity
            }
        }

        // Observe error messages
        userViewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Log.e(TAG, "Registration error: $message")
                showToast("Error: $message")
            }
        }

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateInput(email, password)) {
                registerUser(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter a valid email")
                false
            }
            password.length < 8 -> {
                showToast("Password must be at least 8 characters long")
                false
            }
            else -> true
        }
    }

    private fun registerUser(email: String, password: String) {
        Log.d(TAG, "Attempting to register user: $email")
        userViewModel.signup(email, password)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "RegistrationActivity"
    }
}