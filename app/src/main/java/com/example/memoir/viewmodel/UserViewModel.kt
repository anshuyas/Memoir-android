package com.example.memoir.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memoir.repository.UserRepositoryImpl

class UserViewModel(private val repo: UserRepositoryImpl) : ViewModel() {

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData for login success/failure (one-time event)
    private val _isLoginSuccessful = SingleLiveEvent<Boolean>()
    val isLoginSuccessful: LiveData<Boolean> get() = _isLoginSuccessful

    // LiveData for registration success/failure (one-time event)
    private val _isRegistrationSuccessful = SingleLiveEvent<Boolean>()
    val isRegistrationSuccessful: LiveData<Boolean> get() = _isRegistrationSuccessful

    // LiveData for password reset success/failure (one-time event)
    private val _isPasswordResetSuccessful = SingleLiveEvent<Boolean>()
    val isPasswordResetSuccessful: LiveData<Boolean> get() = _isPasswordResetSuccessful

    // LiveData for error messages (one-time event)
    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    /**
     * Resets the error message to an empty string.
     */
    private fun resetError() {
        _errorMessage.postValue("")
    }

    /**
     * Attempts to log in the user with the provided email and password.
     */
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.postValue("Email and password are required")
            return
        }

        _loading.postValue(true)
        resetError()

        repo.login(email, password) { success, message ->
            _loading.postValue(false)
            if (success) {
                _isLoginSuccessful.postValue(true)
            } else {
                _isLoginSuccessful.postValue(false)
                _errorMessage.postValue(message)
            }
        }
    }

    /**
     * Attempts to register a new user with the provided email and password.
     */
    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.postValue("Email and password are required")
            return
        }

        _loading.postValue(true)
        resetError()

        repo.signup(email, password) { success, userId, message ->
            _loading.postValue(false)
            if (success) {
                _isRegistrationSuccessful.postValue(true)
            } else {
                _isRegistrationSuccessful.postValue(false)
                _errorMessage.postValue(message)
            }
        }
    }

    /**
     * Attempts to send a password reset email to the provided email address.
     */
    fun forgetPassword(email: String) {
        if (email.isEmpty()) {
            _errorMessage.postValue("Email is required")
            return
        }

        repo.forgetPassword(email) { success, message ->
            _loading.postValue(false)
            if (success) {
                _isPasswordResetSuccessful.postValue(true)
                _errorMessage.postValue("Password reset email sent! Check your inbox.")
            } else {
                _isPasswordResetSuccessful.postValue(false)
                _errorMessage.postValue(message ?: "Failed to send password reset email.")
            }
        }
    }
}