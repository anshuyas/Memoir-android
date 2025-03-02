package com.example.memoir.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memoir.R
import com.example.memoir.databinding.ActivityForgotPasswordBinding
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.viewmodel.UserViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding.btnForget.setOnClickListener {
            val email = binding.editEmailForget.text.toString().trim()

            if (validateEmail(email)) {
                userViewModel.forgetPassword(email) { success, message ->
                    if (success) {
                        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Added back to login button functionality
        binding.btnBackToLogin.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateEmail(email: String): Boolean {
        // Check if email is empty
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}