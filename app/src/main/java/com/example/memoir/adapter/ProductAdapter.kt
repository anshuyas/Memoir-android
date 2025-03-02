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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val context: Context,
    private val products: ArrayList<ProductModel> = ArrayList()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface ProductClickListener {
        fun onAddToCartClicked(product: ProductModel, position: Int)
        fun onWishlistClicked(product: ProductModel, position: Int)
        fun onProductClicked(product: ProductModel, position: Int)
    }

    private var listener: ProductClickListener? = null
    private val wishlistedItems = HashSet<String>() // Track wishlisted items by productId

    fun setOnProductClickListener(listener: ProductClickListener) {
        this.listener = listener
    }

    fun updateProducts(newProducts: List<ProductModel>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Set product details
        holder.productName.text = product.productName
        holder.productDesc.text = product.productDesc

        // Format price with currency symbol
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        holder.productPrice.text = formatter.format(product.price)

        holder.productImage.setImageResource(product.imageRes)

        // Set wishlist button state
        if (wishlistedItems.contains(product.productId)) {
            holder.btnWishlist.setImageResource(R.drawable.ic_wishlist)
            holder.btnWishlist.setColorFilter(context.getColor(R.color.heart_color))
        } else {
            holder.btnWishlist.setImageResource(R.drawable.ic_wishlist)
            holder.btnWishlist.setColorFilter(context.getColor(R.color.text_secondary))
        }

        // Set click listeners
        holder.btnAddToCart.setOnClickListener {
            listener?.onAddToCartClicked(product, holder.adapterPosition)
        }

        holder.btnWishlist.setOnClickListener {
            if (wishlistedItems.contains(product.productId)) {
                wishlistedItems.remove(product.productId)
                holder.btnWishlist.setColorFilter(context.getColor(R.color.text_secondary))
            } else {
                wishlistedItems.add(product.productId)
                holder.btnWishlist.setColorFilter(context.getColor(R.color.heart_color))
            }
            listener?.onWishlistClicked(product, holder.adapterPosition)
        }

        holder.productCard.setOnClickListener {
            listener?.onProductClicked(product, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productCard: MaterialCardView = itemView.findViewById(R.id.productCard)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDesc: TextView = itemView.findViewById(R.id.productDesc)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val btnAddToCart: MaterialButton = itemView.findViewById(R.id.btnAddToCart)
        val btnWishlist: ImageButton = itemView.findViewById(R.id.btnWishlist)
    }
}