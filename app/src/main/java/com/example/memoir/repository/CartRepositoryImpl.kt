package com.example.memoir.repository

import com.example.memoir.model.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartRepositoryImpl : CartRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("carts")

    private fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    override fun addToCart(cartModel: CartModel, callback: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        val cartId = ref.child(userId).push().key ?: return callback(false, "Failed to generate cart ID")

        val newCartModel = cartModel.copy(cartId = cartId, userId = userId) // Ensure correct userId is stored

        ref.child(userId).child(cartId).setValue(newCartModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Added to cart")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun removeFromCart(cartId: String, callback: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        ref.child(userId).child(cartId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Removed from cart")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun updateCartItem(cartId: String, quantity: Int, callback: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        ref.child(userId).child(cartId).child("quantity").setValue(quantity).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Cart updated")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getCartItems(userId: String, callback: (List<CartModel>?, Boolean, String) -> Unit) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartModel>()
                for (item in snapshot.children) {
                    val cartItem = item.getValue(CartModel::class.java)
                    if (cartItem != null) {
                        cartItems.add(cartItem)
                    }
                }
                callback(cartItems, true, "Cart items fetched")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }
}
