package com.example.memoir.repository

import com.example.memoir.model.WishlistModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WishlistRepositoryImpl : WishlistRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("wishlists")

    override fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        val wishlistId = ref.push().key.toString()
        wishlistModel.wishlistId = wishlistId
        ref.child(wishlistId).setValue(wishlistModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Added to wishlist")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        ref.child(wishlistId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Removed from wishlist")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getWishlistItems(userId: String, callback: (List<WishlistModel>?, Boolean, String) -> Unit) {
        ref.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
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