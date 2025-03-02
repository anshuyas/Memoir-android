package com.example.memoir.repository

import com.example.memoir.model.ProductModel

interface ProductRepository {
    fun getProductById(productId: String, callback: (ProductModel?, Boolean) -> Unit)
}