package com.example.memoir.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memoir.R
import com.example.memoir.adapter.ProductAdapter
import com.example.memoir.databinding.FragmentHomeBinding
import com.example.memoir.model.ProductModel
import com.example.memoir.model.CartModel
import com.example.memoir.model.WishlistModel
import com.example.memoir.repository.CartRepositoryImpl
import com.example.memoir.repository.UserRepository
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.repository.WishlistRepositoryImpl


class HomeFragment : Fragment(), ProductAdapter.ProductClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter
    private val cartRepo = CartRepositoryImpl()
    private val wishlistRepo = WishlistRepositoryImpl()
    private val userRepo = UserRepositoryImpl()

    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = userRepo.getCurrentUser().toString()
        setupRecyclerView()
        loadProductList()

        binding.swipeRefresh.setOnRefreshListener {
            loadProductList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(requireContext())
        productAdapter.setOnProductClickListener(this) // Fix: Set listener properly

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
        }
    }

    private fun loadProductList() {
        val productList = listOf(
            ProductModel("1", "Aqua Ring", 4.99, R.drawable.bluering),
            ProductModel("2", "Couple Ring", 8.50, R.drawable.couplering),
            ProductModel("3", "Leaf Ring", 6.33, R.drawable.leafring),
            ProductModel("4", "Moon Ring", 5.99, R.drawable.moonring),
            ProductModel("5", "Bow Necklace", 14.99, R.drawable.bownecklace),
            ProductModel("6", "Pearl Necklace",  20.99, R.drawable.pearlnecklace),
            ProductModel("7", "Silver Heart Necklace",  12.99, R.drawable.silverheart),
            ProductModel("8", "Swarovski ",  45.99, R.drawable.swarovski),
            ProductModel("9", "Gold Bracelet",  36.99, R.drawable.goldbangle),
            ProductModel("10", "Diamond Bracelet",  48.99, R.drawable.diamongbangle),
            ProductModel("11", "Pink Bracelet",  26.99, R.drawable.pinkbangle),
            ProductModel("12", "Star Bracelet",  15.99, R.drawable.startbangle),
            ProductModel("13", "Bow Earring",  5.50, R.drawable.bowtop),
            ProductModel("14", "Butterfly Earring",  6.70, R.drawable.butterflytop),
            ProductModel("15", "Heart Earring",  8.50, R.drawable.hearttop),
            ProductModel("16", "Flower Earring",  3.50, R.drawable.flowertop),
        )

        productAdapter.updateProducts(productList)
        toggleEmptyState(productList.isEmpty())
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onAddToCartClicked(product: ProductModel, position: Int) {
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Please sign in to add items to your cart", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = CartModel(
            cartId = "", // Will be assigned in CartRepositoryImpl
            userId = currentUserId!!,
            productId = product.productId,
            quantity = 1,
            price = product.price
        )

        cartRepo.addToCart(cartItem) { success, message ->
            Toast.makeText(requireContext(), if (success) "${product.productName} added to cart" else message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onWishlistClicked(product: ProductModel, position: Int) {
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Please sign in to save items to your wishlist", Toast.LENGTH_SHORT).show()
            return
        }

        val wishlistItem = WishlistModel(
            wishlistId = "", // Generated in WishlistRepositoryImpl
            userId = currentUserId!!,
            productId = product.productId
        )

        wishlistRepo.addToWishlist(wishlistItem) { success, message ->
            Toast.makeText(requireContext(), if (success) "${product.productName} added to wishlist" else message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onProductClicked(product: ProductModel, position: Int) {
        Toast.makeText(requireContext(), "Exploring ${product.productName}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
