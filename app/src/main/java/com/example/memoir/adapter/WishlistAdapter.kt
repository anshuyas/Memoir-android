package com.example.memoir.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memoir.R
import com.example.memoir.model.ProductModel
import com.example.memoir.model.WishlistModel
import com.google.android.material.card.MaterialCardView

class WishlistAdapter(
    private val context: Context,
    private val wishlistItems: List<WishlistModel>,
    private val productMap: Map<String, ProductModel>,
    private val onRemoveClick: (String) -> Unit,
    private val onProductClick: (String) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productCard: MaterialCardView = itemView.findViewById(R.id.wishlistProductCard)
        val productImage: ImageView = itemView.findViewById(R.id.wishlistProductImage)
        val productName: TextView = itemView.findViewById(R.id.wishlistProductName)
        val productPrice: TextView = itemView.findViewById(R.id.wishlistProductPrice)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFromWishlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val wishlistItem = wishlistItems[position]
        val product = productMap[wishlistItem.productId]

        // Set product details
        holder.productName.text = product?.productName ?: "Unknown Product"
        holder.productPrice.text = "$${product?.price ?: 0.0}"

        // Load product image
        if (product != null && product.imageRes > 0) {
            holder.productImage.setImageResource(product.imageRes)
        } else {
            holder.productImage.setImageResource(R.drawable.ic_home) // Default image
        }

        // Set click listeners
        holder.btnRemove.setOnClickListener {
            onRemoveClick(wishlistItem.wishlistId)
        }

        holder.productCard.setOnClickListener {
            onProductClick(wishlistItem.productId)
        }
    }

    override fun getItemCount(): Int = wishlistItems.size
}