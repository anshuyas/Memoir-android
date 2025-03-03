package com.example.memoir.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoir.adapter.WishlistAdapter
import com.example.memoir.databinding.FragmentWishlistBinding
import com.example.memoir.model.ProductModel
import com.example.memoir.model.WishlistModel
import com.example.memoir.repository.ProductRepositoryImpl
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.repository.WishlistRepositoryImpl

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var wishlistAdapter: WishlistAdapter
    private val wishlistRepo = WishlistRepositoryImpl()
    private val productRepo = ProductRepositoryImpl()
    private val userRepo = UserRepositoryImpl()
    private val wishlistItems = mutableListOf<WishlistModel>()
    private val productDetails = mutableMapOf<String, ProductModel>()

    private val userId: String?
        get() = userRepo.getCurrentUser()?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId == null) {
            displayLoginPrompt()
            return
        }

        setupRecyclerView()
        fetchWishlist()
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistAdapter(
            requireContext(), wishlistItems, productDetails,
            onRemoveClick = { wishlistId -> removeItem(wishlistId) },
            onProductClick = { productId -> showProductDetails(productId) }
        )

        binding.recyclerViewWi.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wishlistAdapter
        }
    }

    private fun fetchWishlist() {
        userId?.let { uid ->
            wishlistRepo.getWishlistItems(uid) { items, success, message ->
                if (success && items != null) {
                    wishlistItems.clear()
                    wishlistItems.addAll(items)

                    if (wishlistItems.isEmpty()) {
                        toggleViewVisibility()
                    } else {
                        fetchProductDetails()
                    }
                } else {
                    Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                    toggleViewVisibility()
                }
            }
        } ?: displayLoginPrompt()
    }

    private fun fetchProductDetails() {
        if (wishlistItems.isEmpty()) {
            toggleViewVisibility()
            return
        }

        productDetails.clear()
        var loadedCount = 0

        wishlistItems.forEach { item ->
            productRepo.getProductById(item.productId) { product, success ->
                if (success && product != null) {
                    productDetails[product.productId] = product
                }
                if (++loadedCount >= wishlistItems.size) {
                    wishlistAdapter.notifyDataSetChanged()
                    toggleViewVisibility()
                }
            }
        }
    }

    private fun removeItem(wishlistId: String) {
        wishlistRepo.removeFromWishlist(wishlistId) { success, message ->
            if (success) {
                wishlistItems.removeAll { it.wishlistId == wishlistId }
                productDetails.remove(wishlistId)  // Remove from product details map
                wishlistAdapter.notifyDataSetChanged()
                toggleViewVisibility()
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProductDetails(productId: String) {
        Toast.makeText(context, "Selected product: $productId", Toast.LENGTH_SHORT).show()
    }

    private fun displayLoginPrompt() {
        Toast.makeText(requireContext(), "Log in to view wishlist", Toast.LENGTH_SHORT).show()
        binding.recyclerViewWi.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun toggleViewVisibility() {
        if (wishlistItems.isEmpty()) {
            binding.recyclerViewWi.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerViewWi.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
