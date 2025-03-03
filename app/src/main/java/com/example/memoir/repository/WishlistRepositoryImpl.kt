package com.example.memoir.repository

import com.example.memoir.model.WishlistModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class WishlistRepositoryImpl : WishlistRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("wishlists")

    // Get current user's UID
    private fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    override fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        val wishlistId = ref.child(userId).push().key ?: return callback(false, "Failed to generate wishlist ID")

        val newWishlistModel = wishlistModel.copy(wishlistId = wishlistId, userId = userId)

        ref.child(userId).child(wishlistId).setValue(newWishlistModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Added to wishlist")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        ref.child(userId).child(wishlistId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Removed from wishlist")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getWishlistItems(userId: String, callback: (List<WishlistModel>?, Boolean, String) -> Unit) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wishlistItems = mutableListOf<WishlistModel>()
                for (item in snapshot.children) {
                    val wishlistItem = item.getValue(WishlistModel::class.java)
                    if (wishlistItem != null) {
                        wishlistItems.add(wishlistItem)
                    }
                }
                callback(wishlistItems, true, "Wishlist items fetched")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }
}
