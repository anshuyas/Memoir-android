package com.example.memoir.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memoir.model.WishlistModel
import com.example.memoir.repository.WishlistRepository


class WishlistViewModel(private val repo: WishlistRepository) : ViewModel() {

    private val _wishlistItems = MutableLiveData<List<WishlistModel>?>() // Make it nullable
    val wishlistItems: MutableLiveData<List<WishlistModel>?> get() = _wishlistItems // Make it nullable

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    fun addToWishlist(wishlistModel: WishlistModel, callback: (Boolean, String) -> Unit) {
        repo.addToWishlist(wishlistModel, callback)
    }

    fun removeFromWishlist(wishlistId: String, callback: (Boolean, String) -> Unit) {
        repo.removeFromWishlist(wishlistId, callback)
    }

    fun getWishlistItems(userId: String) {
        _loading.postValue(true)
        repo.getWishlistItems(userId) { wishlistItems, success, message ->
            if (success) {
                _wishlistItems.postValue(wishlistItems) // This can now accept null
            }
            _loading.postValue(false)
        }
    }
}