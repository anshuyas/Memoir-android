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
import com.example.memoir.databinding.ActivityRegistrationBinding
import com.example.memoir.model.UserModel
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.utils.LoadingUtils
import com.example.memoir.viewmodel.UserViewModel

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingUtils = LoadingUtils(this)
        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding.togglePassword.setEndIconOnClickListener {
            val inputType = binding.registerPassword.inputType
            if (inputType == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.registerPassword.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                binding.registerPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.registerPassword.setSelection(binding.registerPassword.text?.length ?: 0)
        }

        binding.toggleConfirmPassword.setEndIconOnClickListener {
            val inputType = binding.registerConfirmPassword.inputType
            if (inputType == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.registerConfirmPassword.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                binding.registerConfirmPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.registerConfirmPassword.setSelection(binding.registerConfirmPassword.text?.length ?: 0)
        }


        // Sign-up button click listener
        binding.signUp.setOnClickListener {
            val firstName = binding.registerFname.text.toString().trim()
            val lastName = binding.registerLname.text.toString().trim()
            val email = binding.registerEmail.text.toString().trim()
            val password = binding.registerPassword.text.toString().trim()
            val confirmPassword = binding.registerConfirmPassword.text.toString().trim()
            val phone = binding.registerContact.text.toString().trim()
            val address = binding.registerAddress.text.toString().trim()

            if (validateInputs(firstName, lastName, email, password, confirmPassword, phone, address)) {
                loadingUtils.show()
                userViewModel.signup(email, password) { success, message, userId ->
                    if (success) {
                        val userModel = UserModel(userId, firstName, lastName, address, phone, email)
                        userViewModel.addUserToDatabase(userId, userModel) { success, message ->


                            if (success) {
                                Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                                finish()
                                loadingUtils.dismiss()
                            } else {
                                loadingUtils.dismiss()
                                Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        loadingUtils.dismiss()
                        Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Navigate to LoginActivity
        binding.btnLoginNavigate.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateInputs(
        firstName: String, lastName: String, email: String, password: String,
        confirmPassword: String, phone: String, address: String
    ): Boolean {
        // Check if fields are empty
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate first name - only letters allowed
        if (!firstName.matches("[a-zA-Z]+".toRegex()) || firstName.length < 3) {
            Toast.makeText(this, "Enter a valid first name (letters only, min 3 characters)", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate last name - only letters allowed
        if (!lastName.matches("[a-zA-Z]+".toRegex())) {
            Toast.makeText(this, "Last name must contain only letters", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate password - at least 8 characters with at least one letter and one number
        if (password.length < 8 || !password.matches(".*[A-Za-z].*".toRegex()) ||
            !password.matches(".*[0-9].*".toRegex())) {
            Toast.makeText(this, "Password must be at least 8 characters with letters and numbers", Toast.LENGTH_LONG).show()
            return false
        }

        // Validate confirm password
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate phone number - must be exactly 10 digits
        if (phone.length != 10 || !phone.matches("\\d+".toRegex())) {
            Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate address - must be at least 5 characters and contain at least one letter
        if (address.length <= 5 || !address.matches(".*[a-zA-Z].*".toRegex())) {
            Toast.makeText(this, "Please enter a valid address (min 5 characters, no special characters)", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}