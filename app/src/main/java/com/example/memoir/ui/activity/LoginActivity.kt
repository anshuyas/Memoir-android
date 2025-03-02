package com.example.memoir.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.memoir.R
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.email)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val forgotPasswordLink = findViewById<TextView>(R.id.forgotPasswordLink)

        // Initialize ViewModel with Factory
        val repo = UserRepositoryImpl()
        val factory = UserViewModelFactory(repo)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // Set up click listeners
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        forgotPasswordLink.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Observe ViewModel LiveData
        observeViewModel()
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter a valid email")
                false
            }
            password.isEmpty() -> {
                showToast("Enter a valid password")
                false
            }
            else -> true
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d(TAG, "Attempting to login user: $email")
        userViewModel.login(email, password)
    }

    private fun observeViewModel() {
        userViewModel.isLoginSuccessful.observe(this) { success ->
            if (success) {
                Log.d(TAG, "Login successful, navigating to HomeActivity")
                showToast("Login Successful")
                startActivity(Intent(this, HomeActivity::class.java))
                finish() // Close the current activity
            }
        }

        userViewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
                Log.e(TAG, "Login error: $error")
                showToast("Error: $error")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}