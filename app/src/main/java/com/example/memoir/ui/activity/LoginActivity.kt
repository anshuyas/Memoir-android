package com.example.memoir.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.memoir.databinding.ActivityLoginBinding
import com.example.memoir.utils.LoadingUtils
import com.example.memoir.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingUtils = LoadingUtils(this)
        userViewModel = UserViewModel()

        // Password visibility toggle
        binding.togglePassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.loginPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.loginPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.loginPassword.setSelection(binding.loginPassword.text.length)
        }

        // Login button click listener
        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                loadingUtils.show()
                userViewModel.login(email, password) { success, message ->
                    loadingUtils.dismiss()
                    if (success) {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@LoginActivity, NavigationActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Navigate to Registration
        binding.btnSignupNavigate.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            finish()
        }

        // Navigate to Forgot Password
        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
