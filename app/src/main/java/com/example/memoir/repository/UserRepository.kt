package com.example.memoir.repository

import com.example.memoir.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit)
    fun signup(email: String, password: String, callback: (Boolean, String, String) -> Unit)
    fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String) -> Unit)
    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)
    fun getCurrentUser(): FirebaseUser?
    fun getUserDetails(userId: String, callback: (UserModel?) -> Unit)
    fun updateUserProfile(userModel: UserModel, callback: (Boolean, String) -> Unit)
    fun deleteUserProfile(callback: (Boolean, String) -> Unit)
}