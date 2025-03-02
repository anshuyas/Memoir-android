package com.example.memoir.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.memoir.R
import com.example.memoir.model.CartModel
import com.example.memoir.model.ProductModel
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val context: Context,
    private val cartItems: List<CartModel>,
    private val productMap: Map<String, ProductModel>,
    private val onRemoveClick: (String) -> Unit,
    private val onQuantityChange: (String, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cartProductImage)
        val productName: TextView = itemView.findViewById(R.id.cartProductName)
        val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.cartProductQuantity)
        val btnRemove: MaterialButton = itemView.findViewById(R.id.btnRemove)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecreaseQuantity)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncreaseQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = productMap[cartItem.productId]

        // Set product name and price
        holder.productName.text = product?.productName ?: "Unknown Product"

        // Format price with currency symbol
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        val totalPrice = cartItem.price * cartItem.quantity
        holder.productPrice.text = formatter.format(totalPrice)

        // Set quantity
        holder.productQuantity.text = cartItem.quantity.toString()

        // Load product image
        if (product != null && product.imageRes > 0) {
            holder.productImage.setImageResource(product.imageRes)
        } else {
            holder.productImage.setImageResource(R.drawable.ic_home) // Default image
        }

        // Set click listeners
        holder.btnRemove.setOnClickListener {
            onRemoveClick(cartItem.cartId)
        }

        holder.btnDecrease.setOnClickListener {
            val newQuantity = cartItem.quantity - 1
            if (newQuantity < 1) {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
            } else {
                onQuantityChange(cartItem.cartId, newQuantity)
            }
        }

        holder.btnIncrease.setOnClickListener {
            val newQuantity = cartItem.quantity + 1
            onQuantityChange(cartItem.cartId, newQuantity)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}