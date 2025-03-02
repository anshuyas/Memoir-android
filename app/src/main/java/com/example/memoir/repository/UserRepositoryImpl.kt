package com.example.memoir.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl : UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        Log.d(TAG, "Attempting to login user: $email")
        if (email.isEmpty() || password.isEmpty()) {
            callback(false, "Email and password are required")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase Auth login successful for user: $email")
                    callback(true, "Login successful")
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "User not found. Please sign up."
                        else -> task.exception?.localizedMessage ?: "Login failed"
                    }
                    Log.e(TAG, "Firebase Auth login error: $errorMessage", task.exception)
                    callback(false, errorMessage)
                }
            }
    }

    override fun signup(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        Log.d(TAG, "Attempting to signup user: $email")
        if (email.isEmpty() || password.isEmpty()) {
            callback(false, "", "Email and password are required")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: ""
                    Log.d(TAG, "Firebase Auth signup successful for user: $email, userId: $userId")

                    // Save user data to Firestore
                    saveUserToFirestore(userId, email) { isSuccess, message ->
                        if (isSuccess) {
                            callback(true, userId, "Signup successful")
                        } else {
                            // Cleanup: Delete auth user if Firestore fails
                            deleteAuthUser(userId)
                            callback(false, "", message)
                        }
                    }
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Signup failed"
                    Log.e(TAG, "Firebase Auth signup error: $errorMessage", task.exception)
                    callback(false, "", errorMessage)
                }
            }
    }

    private fun saveUserToFirestore(userId: String, email: String, callback: (Boolean, String) -> Unit) {
        val userMap = hashMapOf(
            "userId" to userId,
            "email" to email
        )

        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "User data saved to Firestore for userId: $userId")
                callback(true, "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firestore user data save error: ${e.message}", e)
                callback(false, "Failed to save user data")
            }
    }

    private fun deleteAuthUser(userId: String) {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Auth user cleanup successful for userId: $userId")
            } else {
                Log.e(TAG, "Auth user cleanup failed for userId: $userId", task.exception)
            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        Log.d(TAG, "Attempting to send password reset email to: $email")
        if (email.isEmpty()) {
            callback(false, "Email is required")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent to: $email")
                    callback(true, "Password reset email sent. Check your inbox.")
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "No user found with this email."
                        else -> task.exception?.localizedMessage ?: "Failed to send password reset email."
                    }
                    Log.e(TAG, "Password reset error: $errorMessage", task.exception)
                    callback(false, errorMessage)
                }
            }
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}