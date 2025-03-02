package com.example.memoir.repository

import com.example.memoir.model.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepositoryImpl : UserRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Login successful")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun signup(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration success", auth.currentUser?.uid.toString())
            } else {
                callback(false, it.exception?.message.toString(), "")
            }
        }
    }

    override fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String) -> Unit) {
        ref.child(userId).setValue(userModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User added to database")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset email sent to $email")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getUserDetails(userId: String, callback: (UserModel?) -> Unit) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel = snapshot.getValue(UserModel::class.java)
                callback(userModel)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    override fun updateUserProfile(userModel: UserModel, callback: (Boolean, String) -> Unit) {
        ref.child(userModel.userId).setValue(userModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated successfully")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun deleteUserProfile(callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            ref.child(userId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            callback(true, "Profile deleted successfully")
                        } else {
                            callback(false, deleteTask.exception?.message.toString())
                        }
                    }
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }
        } else {
            callback(false, "User not logged in")
        }
    }
}


